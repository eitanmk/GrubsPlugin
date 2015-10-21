package com.selfequalsthis.grubsplugin.service.regions;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.*;
import org.mockito.Mockito;

public class RegionsServiceProviderTest {

	private RegionsServiceProvider provider = null;
	private UUID testWorldUniqueId = null;

	@Before
	public void setUp() throws Exception {
		this.testWorldUniqueId = UUID.randomUUID();
		this.provider = new RegionsServiceProvider(Mockito.mock(RegionsService.class));
	}

	@After
	public void tearDown() throws Exception {
		this.testWorldUniqueId = null;
		this.provider = null;
	}

	@Test
	public void testCreateUniqueRegion() {
		String regionName = "test";
		boolean result = this.provider.createRegion(this.testWorldUniqueId, regionName);
		assertTrue("unique regions should be created successfully", result);
	}

	@Test
	public void testCreateDuplicateRegion() {
		String regionName = "test";
		this.provider.createRegion(this.testWorldUniqueId, regionName);
		boolean result = this.provider.createRegion(this.testWorldUniqueId, regionName);
		assertFalse("duplicate name regions can't be created", result);
	}

	@Test
	public void testCompleteRegion() {
		String regionName = "test";
		this.provider.createRegion(this.testWorldUniqueId, regionName);
		this.provider.addVertex(this.testWorldUniqueId, regionName, 0, 0);
		this.provider.addVertex(this.testWorldUniqueId, regionName, 10, 0);
		this.provider.addVertex(this.testWorldUniqueId, regionName, 10, 10);
		boolean result = this.provider.completeRegion(this.testWorldUniqueId, regionName);
		assertTrue("completing a valid region should succeed", result);
	}

	@Test
	public void testCompleteRegionWithTooFewVertices() {
		String regionName = "test";
		this.provider.createRegion(this.testWorldUniqueId, regionName);
		this.provider.addVertex(this.testWorldUniqueId, regionName, 0, 0);
		boolean result = this.provider.completeRegion(this.testWorldUniqueId, regionName);
		assertFalse("completing a region with too few vertices should fail", result);
	}

	@Test
	public void testAddVertex() {
		String regionName = "test";
		this.provider.createRegion(this.testWorldUniqueId, regionName);
		boolean result = this.provider.addVertex(this.testWorldUniqueId, regionName, 0, 0);
		assertTrue("adding a point to a region should succeed", result);
	}

	@Test
	public void testAddVertexToUndefinedRegion() {
		String regionName = "test";
		boolean result = this.provider.addVertex(this.testWorldUniqueId, regionName, 0, 0);
		assertFalse("adding a point to an undefined region should fail", result);
	}

	@Test
	public void testAddVertexToCompleteRegion() {
		String regionName = "test";
		this.provider.createRegion(this.testWorldUniqueId, regionName);
		this.provider.addVertex(this.testWorldUniqueId, regionName, 0, 0);
		this.provider.addVertex(this.testWorldUniqueId, regionName, 10, 0);
		this.provider.addVertex(this.testWorldUniqueId, regionName, 10, 10);
		this.provider.completeRegion(this.testWorldUniqueId, regionName);
		boolean result = this.provider.addVertex(this.testWorldUniqueId, regionName, 0, 10);
		assertFalse("adding a point to a completed region should fail", result);
	}

	@Test
	public void testDeleteRegion() {
		String regionName = "test";
		this.provider.createRegion(this.testWorldUniqueId, regionName);
		boolean result = this.provider.deleteRegion(this.testWorldUniqueId, regionName);
		assertTrue("deleting an existing region should succeed", result);
	}

	@Test
	public void testDeleteUndefinedRegion() {
		String regionName = "test";
		boolean result = this.provider.deleteRegion(this.testWorldUniqueId, regionName);
		assertFalse("deleting an undefined region should fail", result);
	}

	@Test
	public void testGetRegion() {
		String regionName = "test";
		this.provider.createRegion(this.testWorldUniqueId, regionName);
		this.provider.addVertex(this.testWorldUniqueId, regionName, 0, 0);
		this.provider.addVertex(this.testWorldUniqueId, regionName, 10, 0);
		this.provider.addVertex(this.testWorldUniqueId, regionName, 10, 10);
		this.provider.addVertex(this.testWorldUniqueId, regionName, 0, 10);
		this.provider.completeRegion(this.testWorldUniqueId, regionName);
		String result = this.provider.getRegion(this.testWorldUniqueId, 5, 5, false);
		assertTrue("finding a region by location in the region should succeed", result.equals(regionName));
	}

}
