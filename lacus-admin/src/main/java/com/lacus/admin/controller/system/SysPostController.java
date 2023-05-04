package com.lacus.admin.controller.system;

import com.lacus.common.core.base.BaseController;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.utils.poi.CustomExcelUtil;
import com.lacus.domain.common.command.BulkOperationCommand;
import com.lacus.domain.system.post.command.AddPostCommand;
import com.lacus.domain.system.post.dto.PostDTO;
import com.lacus.domain.system.post.PostApplicationService;
import com.lacus.domain.system.post.query.PostQuery;
import com.lacus.domain.system.post.command.UpdatePostCommand;
import com.lacus.core.annotations.AccessLog;
import com.lacus.dao.system.enums.dictionary.BusinessTypeEnum;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 岗位信息操作处理
 */
@RestController
@RequestMapping("/system/post")
@Validated
public class SysPostController extends BaseController {

    @Autowired
    private PostApplicationService postApplicationService;

    /**
     * 获取岗位列表
     */
    @PreAuthorize("@permission.has('system:post:list')")
    @GetMapping("/list")
    public ResponseDTO<PageDTO> list(PostQuery query) {
        PageDTO pageDTO = postApplicationService.getPostList(query);
        return ResponseDTO.ok(pageDTO);
    }

    @AccessLog(title = "岗位管理", businessType = BusinessTypeEnum.EXPORT)
    @PreAuthorize("@permission.has('system:post:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, PostQuery query) {
        PageDTO pageDTO = postApplicationService.getPostList(query);
        CustomExcelUtil.writeToResponse(pageDTO.getRows(), PostDTO.class, response);
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @PreAuthorize("@permission.has('system:post:query')")
    @GetMapping(value = "/{postId}")
    public ResponseDTO<?> getInfo(@PathVariable Long postId) {
        PostDTO post = postApplicationService.getPostInfo(postId);
        return ResponseDTO.ok(post);
    }

    /**
     * 新增岗位
     */
    @PreAuthorize("@permission.has('system:post:add')")
    @AccessLog(title = "岗位管理", businessType = BusinessTypeEnum.ADD)
    @PostMapping
    public ResponseDTO<?> add(@RequestBody AddPostCommand addCommand) {
        postApplicationService.addPost(addCommand);
        return ResponseDTO.ok();
    }

    /**
     * 修改岗位
     */
    @PreAuthorize("@permission.has('system:post:edit')")
    @AccessLog(title = "岗位管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping
    public ResponseDTO<?> edit(@RequestBody UpdatePostCommand updateCommand) {
        postApplicationService.updatePost(updateCommand);
        return ResponseDTO.ok();
    }

    /**
     * 删除岗位
     */
    @PreAuthorize("@permission.has('system:post:remove')")
    @AccessLog(title = "岗位管理", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping("/{postIds}")
    public ResponseDTO<?> remove(@PathVariable List<Long> postIds) {
        postApplicationService.deletePost(new BulkOperationCommand<>(postIds));
        return ResponseDTO.ok();
    }

}
