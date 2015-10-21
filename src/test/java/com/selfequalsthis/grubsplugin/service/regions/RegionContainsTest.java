package com.selfequalsthis.grubsplugin.service.regions;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.*;

public class RegionContainsTest {

	private Region testRegion = null;
	private UUID testWorldUniqueId = null;

	private void createRegularPolygon() {
		this.testRegion.addVertex(0, 0);
		this.testRegion.addVertex(0, 10);
		this.testRegion.addVertex(10, 10);
		this.testRegion.addVertex(10, 0);
		this.testRegion.addVertex(5, -5);
		this.testRegion.complete();
	}

	@Before
	public void setUp() throws Exception {
		this.testWorldUniqueId = UUID.randomUUID();
		this.testRegion = new Region("testRegion", this.testWorldUniqueId);
	}

	@After
	public void tearDown() throws Exception {
		this.testWorldUniqueId = null;
		this.testRegion = null;
	}

	@Test
	public void testContainsOnIncompleteRegion() {
		assertFalse("Regions not marked as complete contain nothing", this.testRegion.containsLocation(1, 1, this.testWorldUniqueId, false));
	}
	
	@Test
	public void testContainsLocationInsidePolygon() {
		createRegularPolygon();
		assertTrue("Point expected to reside inside the region", this.testRegion.containsLocation(1, 1, this.testWorldUniqueId, false));
	}

	@Test
	public void testDoesNotContainLocationInsidePolygon() {
		createRegularPolygon();
		assertFalse("Point expected to reside outside the region", this.testRegion.containsLocation(-1, 15, this.testWorldUniqueId, false));
	}

	@Test
	public void testContainsLocationInsideBoundingBox() {
		createRegularPolygon();
		assertTrue("Point expected to reside inside the region's bounding box",
				this.testRegion.containsLocation(1, 1, this.testWorldUniqueId, true));
	}

	@Test 
	public void testDoesNotContainLocationInsideBoundingBox() {
		createRegularPolygon();
		assertFalse("Point expected to reside outside the region's bounding box",
				this.testRegion.containsLocation(-10, -10, this.testWorldUniqueId, true));
	}

	@Test 
	public void testContainsLocationOutsidePolygonButWithinBoundingBox() {
		createRegularPolygon();
		assertTrue("Point expected to reside outside polygon, but within bounding box",
				this.testRegion.containsLocation(1, -4, this.testWorldUniqueId, true));
	}
}
