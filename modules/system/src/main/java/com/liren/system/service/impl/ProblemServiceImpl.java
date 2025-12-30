package com.liren.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liren.common.core.result.ResultCode;
import com.liren.system.dto.ProblemDTO;
import com.liren.system.entity.ProblemEntity;
import com.liren.system.entity.ProblemTagRelationEntity;
import com.liren.system.exception.ProblemException;
import com.liren.system.mapper.ProblemMapper;
import com.liren.system.mapper.ProblemTagRelationMapper;
import com.liren.system.service.IProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, ProblemEntity> implements IProblemService {
    @Autowired
    private ProblemTagRelationMapper problemTagRelationMapper;

    /**
     * 新增题目
     * @param problemDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务，保证原子性
    public boolean addProblem(ProblemDTO problemDTO) {
        // =============== 检查标题是否已存在 ===============
        // 如果是新增（ID为空），或者更新（ID不为空），都需要校验标题唯一性
        LambdaQueryWrapper<ProblemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProblemEntity::getTitle, problemDTO.getTitle());

        // 如果是更新操作，需要排除掉“自己”，否则自己改自己会报错
        if (problemDTO.getProblemId() != null) {
            queryWrapper.ne(ProblemEntity::getProblemId, problemDTO.getProblemId());
        }

        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new ProblemException(ResultCode.SUBJECT_TITLE_EXIST);
        }
        // ===========================================

        // 1. DTO转Entity，需要单独设置状态
        ProblemEntity problemEntity = new ProblemEntity();
        BeanUtil.copyProperties(problemDTO, problemEntity);
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
        List<Long> tagIds = problemDTO.getTagIds();
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
}
