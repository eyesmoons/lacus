package com.lacus.domain.lakeintelligence;

import com.lacus.core.security.AuthenticationUtils;
import com.lacus.core.web.domain.login.LoginUser;
import com.lacus.dao.lakeintelligence.entity.LakeModelInfoEntity;
import com.lacus.domain.lakeintelligence.command.CreateModelRequest;
import com.lacus.domain.lakeintelligence.dto.ModelInfoDTO;
import com.lacus.service.lakeintelligence.ILakeDatasetService;
import com.lacus.service.lakeintelligence.ILakeModelInfoService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ModelBusinessTest {

    private ModelBusiness modelBusiness;
    private ILakeModelInfoService lakeModelInfoService;
    private ILakeDatasetService lakeDatasetService;

    @Before
    public void setUp() {
        modelBusiness = new ModelBusiness();
        lakeModelInfoService = mock(ILakeModelInfoService.class);
        ReflectionTestUtils.setField(modelBusiness, "lakeModelInfoService", lakeModelInfoService);
        lakeDatasetService = mock(ILakeDatasetService.class);
        ReflectionTestUtils.setField(modelBusiness, "lakeDatasetService", lakeDatasetService);

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(42L);
        loginUser.setUsername("testuser");
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(loginUser, null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void createModel_shouldSetCreatorIdFromSecurityContext() {
        CreateModelRequest request = new CreateModelRequest();
        request.setModelName("test-model");
        request.setDescription("test description");
        request.setDatasetId(1L);

        when(lakeModelInfoService.save(any(LakeModelInfoEntity.class))).thenAnswer(invocation -> {
            LakeModelInfoEntity entity = invocation.getArgument(0);
            entity.setModelId(100L);
            return true;
        });

        ModelInfoDTO result = modelBusiness.createModel(request);

        assertNotNull(result);
        assertEquals("test-model", result.getModelName());

        verify(lakeModelInfoService).save(argThat(entity -> {
            assertEquals("42", entity.getCreatorId());
            return true;
        }));
    }
}
