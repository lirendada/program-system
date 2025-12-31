package com.liren.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liren.common.core.enums.ProblemStatusEnum;
import com.liren.common.core.result.ResultCode;
import com.liren.system.dto.ProblemAddDTO;
import com.liren.system.dto.ProblemQueryRequest;
import com.liren.system.entity.ProblemEntity;
import com.liren.system.entity.ProblemTagEntity;
import com.liren.system.entity.ProblemTagRelationEntity;
import com.liren.system.exception.ProblemException;
import com.liren.system.mapper.ProblemMapper;
import com.liren.system.mapper.ProblemTagMapper;
import com.liren.system.mapper.ProblemTagRelationMapper;
import com.liren.system.service.IProblemService;
import com.liren.system.vo.ProblemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, ProblemEntity> implements IProblemService {
    @Autowired
    private ProblemTagRelationMapper problemTagRelationMapper;

    @Autowired
    private ProblemTagMapper problemTagMapper;

    /**
     * 新增题目
     * @param problemAddDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务，保证原子性
    public boolean addProblem(ProblemAddDTO problemAddDTO) {
        // =============== 检查标题是否已存在 ===============
        // 如果是新增（ID为空），或者更新（ID不为空），都需要校验标题唯一性
        LambdaQueryWrapper<ProblemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProblemEntity::getTitle, problemAddDTO.getTitle());

        // 如果是更新操作，需要排除掉“自己”，否则自己改自己会报错
        if (problemAddDTO.getProblemId() != null) {
            queryWrapper.ne(ProblemEntity::getProblemId, problemAddDTO.getProblemId());
        }

        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new ProblemException(ResultCode.SUBJECT_TITLE_EXIST);
        }
        // ===========================================

        // 1. DTO转Entity，需要单独设置状态
        ProblemEntity problemEntity = new ProblemEntity();
        BeanUtil.copyProperties(problemAddDTO, problemEntity);
        if(problemEntity.getStatus() == null) {
            problemEntity.setStatus(1);
        }

        // 2. 保存实体
        boolean isSave = this.save(problemEntity);
        if (!isSave) {
            throw new ProblemException(ResultCode.SUBJECT_NOT_FOUND);
        }

        // 3. 处理标签关系(如果有)
        Long problemId = problemEntity.getProblemId();
        List<Long> tagIds = problemAddDTO.getTagIds();
        if(CollectionUtil.isNotEmpty(tagIds)) {
            List<ProblemTagRelationEntity> relationList = tagIds.stream().map(tagId -> {
                ProblemTagRelationEntity problemTagRelationEntity = new ProblemTagRelationEntity();
                problemTagRelationEntity.setProblemId(problemId);
                problemTagRelationEntity.setTagId(tagId);
                return problemTagRelationEntity;
            }).collect(Collectors.toList());

            // 保存标签关系
            for(ProblemTagRelationEntity relation : relationList) {
                problemTagRelationMapper.insert(relation);
            }
        }

        return true;
    }

    /**
     * 分页查询题目列表 (支持多标签筛选 + 批量填充)
     */
    @Override
    public Page<ProblemVO> getProblemList(ProblemQueryRequest queryRequest) {
        // === 1. 处理标签筛选 (Filtering) ===
        Set<Long> filterProblemIds = null;
        if(CollectionUtil.isNotEmpty(queryRequest.getTags())) {
            List<String> tags = queryRequest.getTags();

            // 1.1 查出所有标签id
            LambdaQueryWrapper<ProblemTagEntity> tagWrapper = new LambdaQueryWrapper<>();
            tagWrapper.in(ProblemTagEntity::getTagId, tags);
            List<ProblemTagEntity> tagEntities = problemTagMapper.selectList(tagWrapper);

            // 如果数据库里存在的标签数量 < 用户请求的数量，说明必然无法满足 "包含所有标签" 的条件
            if(tagEntities.size() < tags.size()) {
                return new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize());
            }
            List<Long> tagIds = tagEntities.stream().map(ProblemTagEntity::getTagId).collect(Collectors.toList());

            // 1.2 找出同时包含这些 tagId 的题目id
            // 查出所有关联记录
            LambdaQueryWrapper<ProblemTagRelationEntity> relationWrapper = new LambdaQueryWrapper<>();
            relationWrapper.in(ProblemTagRelationEntity::getTagId, tagIds);
            List<ProblemTagRelationEntity> problemTagRelationEntities = problemTagRelationMapper.selectList(relationWrapper);

            // 分组统计每个problemId的出现次数：Map<ProblemId, 命中标签次数>
            Map<Long, Long> collect = problemTagRelationEntities.stream()
                    .collect(Collectors.groupingBy(ProblemTagRelationEntity::getProblemId, Collectors.counting()));

            // 只有命中次数 == 请求标签数量的题目，才是我们要的题目
            filterProblemIds = collect.entrySet().stream()
                    .filter(entry -> entry.getValue() == tags.size())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
            if(filterProblemIds.isEmpty()) {
                return new Page<>(queryRequest.getCurrent(), queryRequest.getPageSize());
            }
        }

        // === 2. 构建主查询 (Main Query) ===
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();
        LambdaQueryWrapper<ProblemEntity> wrapper = new LambdaQueryWrapper<>();

        // 标签筛选
        if(filterProblemIds != null) {
            wrapper.in(ProblemEntity::getProblemId, filterProblemIds);
        }

        // 题目id匹配
        if(queryRequest.getProblemId() != null) {
            wrapper.eq(ProblemEntity::getProblemId, queryRequest.getProblemId());
        }

        // 难度匹配
        if(queryRequest.getDifficulty() != null) {
            wrapper.eq(ProblemEntity::getDifficulty, queryRequest.getDifficulty());
        }

        // 标题/内容模糊匹配(同时查标题或内容)
        String keyword = queryRequest.getKeyword();
        if(StringUtils.hasText(keyword)) {
            // 注意这里要先and，再or，而不能直接or
            wrapper.and(qw -> qw.like(ProblemEntity::getTitle, keyword))
                    .or()
                    .like(ProblemEntity::getDescription, keyword);
        }

        // 排除已删除 (逻辑删除MP会自动处理，这里只处理 status=0 隐藏的情况)
        wrapper.eq(ProblemEntity::getStatus, ProblemStatusEnum.NORMAL.getCode());

        // 处理排序情况
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();
        if (StringUtils.hasText(sortField)) {
            // 这里的 equals 要根据你具体的业务需求，比如前端传 "createTime"
            boolean isAsc = "ascend".equals(sortOrder);
            if ("createTime".equals(sortField)) {
                wrapper.orderBy(true, isAsc, ProblemEntity::getCreateTime);
            } else if ("submitNum".equals(sortField)) {
                wrapper.orderBy(true, isAsc, ProblemEntity::getSubmitNum);
            }
        } else {
            // 默认排序：按创建时间倒序
            wrapper.orderByDesc(ProblemEntity::getCreateTime);
        }

        // ================= 3. 执行分页查询 =================
        Page<ProblemEntity> problemEntityPage = this.page(new Page<>(current, size), wrapper);
        List<ProblemEntity> records = problemEntityPage.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return new Page<>(current, size, problemEntityPage.getTotal());
        }

        // ================= 4. 批量填充标签 (Filling) =================


        // ================= 5. 组装 VO =================
        Page<ProblemVO> problemVOPage = new Page<>(current, size, problemEntityPage.getTotal());
        List<ProblemVO> list = problemEntityPage.getRecords().stream()
                .map(ProblemVO::objToVo)
                .collect(Collectors.toList());
        problemVOPage.setRecords(list);
        return problemVOPage;
    }
}
