package com.selfequalsthis.grubsplugin.service.regions;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.*;

public class RegionCreateTest {
	
	private Region testRegion = null;
	private UUID testWorldUniqueId = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
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
		this.testRegion.addVertex(0, 1);
		this.testRegion.addVertex(1, 1);
		this.testRegion.addVertex(1, 0);
		this.testRegion.complete();
		this.testRegion.addVertex(1, -1);
		assertTrue("No more vertices after region marked complete", this.testRegion.getNumVerticies() == 4);
	}
}
