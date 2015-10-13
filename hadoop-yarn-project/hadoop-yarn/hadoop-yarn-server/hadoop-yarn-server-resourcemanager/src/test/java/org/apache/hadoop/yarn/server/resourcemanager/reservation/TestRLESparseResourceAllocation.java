/*******************************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *******************************************************************************/
package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.hadoop.yarn.api.records.ReservationRequest;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRLESparseResourceAllocation {

  private static final Logger LOG = LoggerFactory
      .getLogger(TestRLESparseResourceAllocation.class);

  @Test
  public void testBlocks() {
    ResourceCalculator resCalc = new DefaultResourceCalculator();
    Resource minAlloc = Resource.newInstance(1, 1);

    RLESparseResourceAllocation rleSparseVector =
        new RLESparseResourceAllocation(resCalc, minAlloc);
    int[] alloc = { 10, 10, 10, 10, 10, 10 };
    int start = 100;
    Set<Entry<ReservationInterval, Resource>> inputs =
        generateAllocation(start, alloc, false).entrySet();
    for (Entry<ReservationInterval, Resource> ip : inputs) {
      rleSparseVector.addInterval(ip.getKey(), ip.getValue());
    }
    LOG.info(rleSparseVector.toString());
    Assert.assertFalse(rleSparseVector.isEmpty());
    Assert.assertEquals(Resource.newInstance(0, 0),
        rleSparseVector.getCapacityAtTime(99));
    Assert.assertEquals(Resource.newInstance(0, 0),
        rleSparseVector.getCapacityAtTime(start + alloc.length + 1));
    for (int i = 0; i < alloc.length; i++) {
      Assert.assertEquals(Resource.newInstance(1024 * (alloc[i]), (alloc[i])),
          rleSparseVector.getCapacityAtTime(start + i));
    }
    Assert.assertEquals(Resource.newInstance(0, 0),
        rleSparseVector.getCapacityAtTime(start + alloc.length + 2));
    for (Entry<ReservationInterval, Resource> ip : inputs) {
      rleSparseVector.removeInterval(ip.getKey(), ip.getValue());
    }
    LOG.info(rleSparseVector.toString());
    for (int i = 0; i < alloc.length; i++) {
      Assert.assertEquals(Resource.newInstance(0, 0),
          rleSparseVector.getCapacityAtTime(start + i));
    }
    Assert.assertTrue(rleSparseVector.isEmpty());
  }

  @Test
  public void testSteps() {
    ResourceCalculator resCalc = new DefaultResourceCalculator();
    Resource minAlloc = Resource.newInstance(1, 1);

    RLESparseResourceAllocation rleSparseVector =
        new RLESparseResourceAllocation(resCalc, minAlloc);
    int[] alloc = { 10, 10, 10, 10, 10, 10 };
    int start = 100;
    Set<Entry<ReservationInterval, Resource>> inputs =
        generateAllocation(start, alloc, true).entrySet();
    for (Entry<ReservationInterval, Resource> ip : inputs) {
      rleSparseVector.addInterval(ip.getKey(), ip.getValue());
    }
    LOG.info(rleSparseVector.toString());
    Assert.assertFalse(rleSparseVector.isEmpty());
    Assert.assertEquals(Resource.newInstance(0, 0),
        rleSparseVector.getCapacityAtTime(99));
    Assert.assertEquals(Resource.newInstance(0, 0),
        rleSparseVector.getCapacityAtTime(start + alloc.length + 1));
    for (int i = 0; i < alloc.length; i++) {
      Assert.assertEquals(
          Resource.newInstance(1024 * (alloc[i] + i), (alloc[i] + i)),
          rleSparseVector.getCapacityAtTime(start + i));
    }
    Assert.assertEquals(Resource.newInstance(0, 0),
        rleSparseVector.getCapacityAtTime(start + alloc.length + 2));
    for (Entry<ReservationInterval, Resource> ip : inputs) {
      rleSparseVector.removeInterval(ip.getKey(),ip.getValue());
    }
    LOG.info(rleSparseVector.toString());
    for (int i = 0; i < alloc.length; i++) {
      Assert.assertEquals(Resource.newInstance(0, 0),
          rleSparseVector.getCapacityAtTime(start + i));
    }
    Assert.assertTrue(rleSparseVector.isEmpty());
  }

  @Test
  public void testSkyline() {
    ResourceCalculator resCalc = new DefaultResourceCalculator();
    Resource minAlloc = Resource.newInstance(1, 1);

    RLESparseResourceAllocation rleSparseVector =
        new RLESparseResourceAllocation(resCalc, minAlloc);
    int[] alloc = { 0, 5, 10, 10, 5, 0 };
    int start = 100;
    Set<Entry<ReservationInterval, Resource>> inputs =
        generateAllocation(start, alloc, true).entrySet();
    for (Entry<ReservationInterval, Resource> ip : inputs) {
      rleSparseVector.addInterval(ip.getKey(), ip.getValue());
    }
    LOG.info(rleSparseVector.toString());
    Assert.assertFalse(rleSparseVector.isEmpty());
    Assert.assertEquals(Resource.newInstance(0, 0),
        rleSparseVector.getCapacityAtTime(99));
    Assert.assertEquals(Resource.newInstance(0, 0),
        rleSparseVector.getCapacityAtTime(start + alloc.length + 1));
    for (int i = 0; i < alloc.length; i++) {
      Assert.assertEquals(
          Resource.newInstance(1024 * (alloc[i] + i), (alloc[i] + i)),
          rleSparseVector.getCapacityAtTime(start + i));
    }
    Assert.assertEquals(Resource.newInstance(0, 0),
        rleSparseVector.getCapacityAtTime(start + alloc.length + 2));
    for (Entry<ReservationInterval, Resource> ip : inputs) {
      rleSparseVector.removeInterval(ip.getKey(), ip.getValue());
    }
    LOG.info(rleSparseVector.toString());
    for (int i = 0; i < alloc.length; i++) {
      Assert.assertEquals(Resource.newInstance(0, 0),
          rleSparseVector.getCapacityAtTime(start + i));
    }
    Assert.assertTrue(rleSparseVector.isEmpty());
  }

  @Test
  public void testZeroAlloaction() {
    ResourceCalculator resCalc = new DefaultResourceCalculator();
    Resource minAlloc = Resource.newInstance(1, 1);
    RLESparseResourceAllocation rleSparseVector =
        new RLESparseResourceAllocation(resCalc, minAlloc);
    rleSparseVector.addInterval(new ReservationInterval(0, Long.MAX_VALUE),
        Resource.newInstance(0, 0));
    LOG.info(rleSparseVector.toString());
    Assert.assertEquals(Resource.newInstance(0, 0),
        rleSparseVector.getCapacityAtTime(new Random().nextLong()));
    Assert.assertTrue(rleSparseVector.isEmpty());
  }

  @Test
  public void testToIntervalMap() {
    ResourceCalculator resCalc = new DefaultResourceCalculator();
    Resource minAlloc = Resource.newInstance(1, 1);
    RLESparseResourceAllocation rleSparseVector =
        new RLESparseResourceAllocation(resCalc, minAlloc);
    Map<ReservationInterval, Resource> mapAllocations;

    // Check empty
    mapAllocations = rleSparseVector.toIntervalMap();
    Assert.assertTrue(mapAllocations.isEmpty());

    // Check full
    int[] alloc = { 0, 5, 10, 10, 5, 0, 5, 0 };
    int start = 100;
    Set<Entry<ReservationInterval, Resource>> inputs =
        generateAllocation(start, alloc, false).entrySet();
    for (Entry<ReservationInterval, Resource> ip : inputs) {
      rleSparseVector.addInterval(ip.getKey(), ip.getValue());
    }
    mapAllocations = rleSparseVector.toIntervalMap();
    Assert.assertTrue(mapAllocations.size() == 5);
    for (Entry<ReservationInterval, Resource> entry : mapAllocations
        .entrySet()) {
      ReservationInterval interval = entry.getKey();
      Resource resource = entry.getValue();
      if (interval.getStartTime() == 101L) {
        Assert.assertTrue(interval.getEndTime() == 102L);
        Assert.assertEquals(resource, Resource.newInstance(5 * 1024, 5));
      } else if (interval.getStartTime() == 102L) {
        Assert.assertTrue(interval.getEndTime() == 104L);
        Assert.assertEquals(resource, Resource.newInstance(10 * 1024, 10));
      } else if (interval.getStartTime() == 104L) {
        Assert.assertTrue(interval.getEndTime() == 105L);
        Assert.assertEquals(resource, Resource.newInstance(5 * 1024, 5));
      } else if (interval.getStartTime() == 105L) {
        Assert.assertTrue(interval.getEndTime() == 106L);
        Assert.assertEquals(resource, Resource.newInstance(0 * 1024, 0));
      } else if (interval.getStartTime() == 106L) {
        Assert.assertTrue(interval.getEndTime() == 107L);
        Assert.assertEquals(resource, Resource.newInstance(5 * 1024, 5));
      } else {
        Assert.fail();
      }
    }
  }

  private Map<ReservationInterval, Resource> generateAllocation(
      int startTime, int[] alloc, boolean isStep) {
    Map<ReservationInterval, Resource> req =
        new HashMap<ReservationInterval, Resource>();
    int numContainers = 0;
    for (int i = 0; i < alloc.length; i++) {
      if (isStep) {
        numContainers = alloc[i] + i;
      } else {
        numContainers = alloc[i];
      }
      req.put(new ReservationInterval(startTime + i, startTime + i + 1),
          ReservationSystemUtil.toResource(ReservationRequest.newInstance(
              Resource.newInstance(1024, 1), (numContainers))));
    }
    return req;
  }

}
