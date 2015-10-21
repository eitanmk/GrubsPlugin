package com.selfequalsthis.grubsplugin.service.regions;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.*;

public class RegionTest {
	
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

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		this.testWorldUniqueId = UUID.randomUUID();
		this.testRegion = new Region(this.testWorldUniqueId, "testRegion");
	}

	@After
	public void tearDown() throws Exception {
		this.testWorldUniqueId = null;
		this.testRegion = null;
	}

	@Test
	public void testNoVerticies() {
		assertTrue("Newly created region has no verticies", this.testRegion.getNumVerticies() == 0);
	}

	@Test
	public void testAddVertex() {
		this.testRegion.addVertex(0, 0);
		assertTrue("Test region should have one vertex", this.testRegion.getNumVerticies() == 1);
	}

	@Test
	public void testAddMultipleVertices() {
		this.testRegion.addVertex(0, 0);
		this.testRegion.addVertex(0, 1);
		this.testRegion.addVertex(1, 1);
		this.testRegion.addVertex(1, 0);
		assertTrue("Test region should have four verticies", this.testRegion.getNumVerticies() == 4);
	}

	@Test
	public void testAddVerticesAfterComplete() {
		this.testRegion.addVertex(0, 0);
		this.testRegion.complete();
		this.testRegion.addVertex(1, -1);
		assertTrue("No more vertices after region marked complete", this.testRegion.getNumVerticies() == 1);
	}

	@Test
	public void testContainsOnIncompleteRegion() {
		assertFalse("Regions not marked as complete contain nothing",
				this.testRegion.containsLocation(this.testWorldUniqueId, 1, 1, false));
	}
	
	@Test
	public void testContainsLocationInsidePolygon() {
		createRegularPolygon();
		assertTrue("Point expected to reside inside the region",
				this.testRegion.containsLocation(this.testWorldUniqueId, 1, 1, false));
	}

	@Test
	public void testDoesNotContainLocationInsidePolygon() {
		createRegularPolygon();
		assertFalse("Point expected to reside outside the region",
				this.testRegion.containsLocation(this.testWorldUniqueId, -1, 15, false));
	}

	@Test
	public void testContainsLocationInsideBoundingBox() {
		createRegularPolygon();
		assertTrue("Point expected to reside inside the region's bounding box",
				this.testRegion.containsLocation(this.testWorldUniqueId, 1, 1, true));
	}

	@Test 
	public void testDoesNotContainLocationInsideBoundingBox() {
		createRegularPolygon();
		assertFalse("Point expected to reside outside the region's bounding box",
				this.testRegion.containsLocation(this.testWorldUniqueId, -10, -10, true));
	}

	@Test 
	public void testContainsLocationOutsidePolygonButWithinBoundingBox() {
		createRegularPolygon();
		assertTrue("Point expected to reside outside polygon, but within bounding box",
				this.testRegion.containsLocation(this.testWorldUniqueId, 1, -4, true));
	}

	@Test
	public void testLocationInDifferentWorld() {
		createRegularPolygon();
		assertFalse("Locations from a different world are irrelevant", 
				this.testRegion.containsLocation(UUID.randomUUID(), 1, 1, false));
	}
}
