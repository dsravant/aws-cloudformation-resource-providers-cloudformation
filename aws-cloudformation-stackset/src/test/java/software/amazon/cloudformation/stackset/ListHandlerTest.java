package software.amazon.cloudformation.stackset;

import software.amazon.awssdk.services.cloudformation.model.DescribeStackInstanceRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStackSetRequest;
import software.amazon.awssdk.services.cloudformation.model.ListStackInstancesRequest;
import software.amazon.awssdk.services.cloudformation.model.ListStackSetsRequest;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static software.amazon.cloudformation.stackset.util.TestUtils.DESCRIBE_SELF_MANAGED_STACK_SET_RESPONSE;
import static software.amazon.cloudformation.stackset.util.TestUtils.DESCRIBE_SERVICE_MANAGED_STACK_SET_RESPONSE;
import static software.amazon.cloudformation.stackset.util.TestUtils.DESCRIBE_STACK_INSTANCE_RESPONSE_1;
import static software.amazon.cloudformation.stackset.util.TestUtils.DESCRIBE_STACK_INSTANCE_RESPONSE_2;
import static software.amazon.cloudformation.stackset.util.TestUtils.DESCRIBE_STACK_INSTANCE_RESPONSE_3;
import static software.amazon.cloudformation.stackset.util.TestUtils.DESCRIBE_STACK_INSTANCE_RESPONSE_4;
import static software.amazon.cloudformation.stackset.util.TestUtils.LIST_SELF_MANAGED_STACK_SET_RESPONSE;
import static software.amazon.cloudformation.stackset.util.TestUtils.LIST_SERVICE_MANAGED_STACK_SET_RESPONSE;
import static software.amazon.cloudformation.stackset.util.TestUtils.LIST_STACK_SETS_RESPONSE;
import static software.amazon.cloudformation.stackset.util.TestUtils.READ_MODEL;
import static software.amazon.cloudformation.stackset.util.TestUtils.SELF_MANAGED_MODEL_FOR_READ;
import static software.amazon.cloudformation.stackset.util.TestUtils.SERVICE_MANAGED_MODEL;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest {

    private ListHandler handler;

    private ResourceHandlerRequest<ResourceModel> request;

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
        handler = new ListHandler();
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
        request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(READ_MODEL)
                .build();
    }

    @Test
    public void handleRequest_SelfManagedSS_Success() {

        doReturn(LIST_STACK_SETS_RESPONSE).when(proxy)
                .injectCredentialsAndInvokeV2(any(ListStackSetsRequest.class), any());
        doReturn(DESCRIBE_SELF_MANAGED_STACK_SET_RESPONSE).when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeStackSetRequest.class), any());
        doReturn(LIST_SELF_MANAGED_STACK_SET_RESPONSE).when(proxy)
                .injectCredentialsAndInvokeV2(any(ListStackInstancesRequest.class), any());

        doReturn(DESCRIBE_STACK_INSTANCE_RESPONSE_1,
                DESCRIBE_STACK_INSTANCE_RESPONSE_2,
                DESCRIBE_STACK_INSTANCE_RESPONSE_3,
                DESCRIBE_STACK_INSTANCE_RESPONSE_4).when(proxy)
                .injectCredentialsAndInvokeV2(any(DescribeStackInstanceRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).containsExactly(SELF_MANAGED_MODEL_FOR_READ);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
