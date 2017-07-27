/*
 * Copyright 2017 flow.ci
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flow.platform.api.test.service;

import com.flow.platform.api.domain.Flow;
import com.flow.platform.api.domain.Node;
import com.flow.platform.api.domain.Step;
import com.flow.platform.api.service.YamlService;
import com.flow.platform.api.test.TestBase;
import com.google.common.io.Files;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lhl
 */
public class YamlServiceTest extends TestBase {

    @Autowired
    private YamlService yamlService;

    private File ymlSampleFile;

    @Before
    public void before() {
        String path = "/Users/fir/Projects/Flow/flow-platform/flow.yaml";
        ClassLoader classLoader = YamlServiceTest.class.getClassLoader();
        URL resource = classLoader.getResource("flow.yaml");
        ymlSampleFile = new File(resource.getFile());
    }

    @Test
    public void should_create_node_by_file() {
        Node node = yamlService.createNode(ymlSampleFile);

        // verify flow
        Assert.assertTrue(node instanceof Flow);
        Assert.assertEquals("flow1", node.getName());
        Assert.assertEquals("/flow1", node.getPath());

        // verify steps
        Flow root = (Flow) node;
        List<Step> steps = root.getChildren();
        Assert.assertEquals(2, steps.size());

        Assert.assertEquals("step1", steps.get(0).getName());
        Assert.assertEquals("/flow1/step1", steps.get(0).getPath());

        Assert.assertEquals("step2", steps.get(1).getName());
        Assert.assertEquals("/flow1/step2", steps.get(1).getPath());

        // verify parent node relationship
        Assert.assertEquals(root, steps.get(0).getParent());
        Assert.assertEquals(root, steps.get(1).getParent());

        // verify prev next node relationship
        Assert.assertEquals(steps.get(1), steps.get(0).getNext());
        Assert.assertEquals(steps.get(0), steps.get(1).getPrev());
    }

    @Test
    public void should_create_node_by_string() throws Throwable {
        String yamlRaw = Files.toString(ymlSampleFile, Charset.forName("UTF-8"));
        Node node = yamlService.createNode(yamlRaw);
        Assert.assertEquals("flow1", node.getName());
    }
}
