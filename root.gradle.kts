plugins {
    id("gg.essential.multi-version.root")
}

//version = versionFromBuildIdAndBranch()

preprocess {
    strictExtraMappings.set(true)

    val fabric12105 = createNode("1.21.5-fabric", 12105, "yarn")
    val fabric12104 = createNode("1.21.4-fabric", 12104, "yarn")
    val fabric12103 = createNode("1.21.3-fabric", 12103, "yarn")
    val fabric12100 = createNode("1.21-fabric", 12100, "yarn")
    val fabric12006 = createNode("1.20.6-fabric", 12006, "yarn")
    val forge12004 = createNode("1.20.4-forge", 12004, "srg")
    val fabric12004 = createNode("1.20.4-fabric", 12004, "yarn")
    val forge12002 = createNode("1.20.2-forge", 12002, "srg")
    val fabric12002 = createNode("1.20.2-fabric", 12002, "yarn")
    val forge12001 = createNode("1.20.1-forge", 12001, "srg")
    val fabric12001 = createNode("1.20.1-fabric", 12001, "yarn")
    val fabric12000 = createNode("1.20-fabric", 12000, "yarn")
    val forge11904 = createNode("1.19.4-forge", 11904, "srg")
    val fabric11904 = createNode("1.19.4-fabric", 11904, "yarn")
    val forge11903 = createNode("1.19.3-forge", 11903, "srg")
    val fabric11903 = createNode("1.19.3-fabric", 11903, "yarn")
    val forge11902 = createNode("1.19.2-forge", 11902, "srg")
    val fabric11902 = createNode("1.19.2-fabric", 11902, "yarn")
    val fabric11900 = createNode("1.19-fabric", 11900, "yarn")
    val forge11802 = createNode("1.18.2-forge", 11802, "srg")
    val fabric11802 = createNode("1.18.2-fabric", 11802, "yarn")
    val fabric11801 = createNode("1.18.1-fabric", 11801, "yarn")
    val forge11701 = createNode("1.17.1-forge", 11701, "srg")
    val fabric11701 = createNode("1.17.1-fabric", 11701, "yarn")
    val fabric11605 = createNode("1.16.5-fabric", 11605, "yarn")
    val forge11605 = createNode("1.16.5-forge", 11605, "srg")
    val forge11202 = createNode("1.12.2-forge", 11202, "srg")
    val forge10809 = createNode("1.8.9-forge", 10809, "srg")

    fabric12105.link(fabric12104)
    fabric12104.link(fabric12103)
    fabric12103.link(fabric12100)
    fabric12100.link(fabric12006)
    fabric12006.link(fabric12004)
    forge12004.link(fabric12004)
    fabric12004.link(fabric12002)
    forge12002.link(fabric12002)
    fabric12002.link(fabric12001)
    forge12001.link(fabric12001)
    fabric12001.link(fabric12000)
    fabric12000.link(fabric11904)
    forge11904.link(fabric11904)
    fabric11904.link(fabric11903)
    forge11903.link(fabric11903)
    fabric11903.link(fabric11902, file("versions/1.19.3-1.19.2.txt"))
    forge11902.link(fabric11902)
    fabric11902.link(fabric11900)
    fabric11900.link(fabric11802)
    forge11802.link(fabric11802)
    fabric11802.link(fabric11801)
    fabric11801.link(fabric11701)
    forge11701.link(fabric11701)
    fabric11701.link(fabric11605)
    fabric11605.link(forge11605)
    forge11605.link(forge11202, file("versions/1.16.2-1.12.2.txt"))
    forge11202.link(forge10809)
}

// Enable reproducible builds
allprojects {
    tasks.withType<AbstractArchiveTask> {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}
