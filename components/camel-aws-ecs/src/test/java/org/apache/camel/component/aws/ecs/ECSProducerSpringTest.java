/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.aws.ecs;

import com.amazonaws.services.ecs.model.CreateClusterResult;
import com.amazonaws.services.ecs.model.DeleteClusterResult;
import com.amazonaws.services.ecs.model.DescribeClustersResult;
import com.amazonaws.services.ecs.model.ListClustersResult;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ECSProducerSpringTest extends CamelSpringTestSupport {
    
    @EndpointInject(uri = "mock:result")
    private MockEndpoint mock;
    
    @Test
    public void kmsListClustersTest() throws Exception {

        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:listClusters", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(ECSConstants.OPERATION, ECSOperations.listClusters);
            }
        });

        assertMockEndpointsSatisfied();
        
        ListClustersResult resultGet = (ListClustersResult) exchange.getIn().getBody();
        assertEquals(1, resultGet.getClusterArns().size());
        assertEquals("Test", resultGet.getClusterArns().get(0));
    }
    
    @Test
    public void ecsCreateClusterTest() throws Exception {

        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:createCluster", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(ECSConstants.OPERATION, ECSOperations.createCluster);
                exchange.getIn().setHeader(ECSConstants.CLUSTER_NAME, "Test");
            }
        });

        assertMockEndpointsSatisfied();
        
        CreateClusterResult resultGet = (CreateClusterResult) exchange.getIn().getBody();
        assertEquals("Test", resultGet.getCluster().getClusterName());
    }
    
    @Test
    public void eksDescribeClusterTest() throws Exception {

        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:describeCluster", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(ECSConstants.OPERATION, ECSOperations.describeCluster);
                exchange.getIn().setHeader(ECSConstants.CLUSTER_NAME, "Test");
            }
        });

        assertMockEndpointsSatisfied();
        
        DescribeClustersResult resultGet = exchange.getIn().getBody(DescribeClustersResult.class);
        assertEquals("Test", resultGet.getClusters().get(0).getClusterName());
    }
    
    @Test
    public void eksDeleteClusterTest() throws Exception {

        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:deleteCluster", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(ECSConstants.OPERATION, ECSOperations.deleteCluster);
                exchange.getIn().setHeader(ECSConstants.CLUSTER_NAME, "Test");
            }
        });

        assertMockEndpointsSatisfied();
        
        DeleteClusterResult resultGet = exchange.getIn().getBody(DeleteClusterResult.class);
        assertEquals("Test", resultGet.getCluster().getClusterName());
    }

    @Override
    protected ClassPathXmlApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("org/apache/camel/component/aws/ecs/ECSComponentSpringTest-context.xml");
    }
}
