fun main(args: Array<String>) {
    val graph = FlowNetwork.build {
        val n1 by it.source
        val n2 by it.node
        val n3 by it.node
        val n4 by it.node
        val n5 by it.node
        val n6 by it.node
        val n7 by it.node
        val n8 by it.node
        val n9 by it.node
        val n10 by it.sink

        n1 - 4 - n6; n1 - 7 - n5; n1 - 9 - n4; n1 - 2 - n2
        n2 - 5 - n4; n2 - 3 - n3
        n3 - 6 - n4; n3 - 5 - n9
        n4 - 7 - n5; n4 - 6 - n8; n4 - 2 - n10; n4 - 4 - n9
        n5 - 5 - n6; n5 - 4 - n8
        n6 - 8 - n8; n6 - 2 - n7
        n7 - 6 - n10
        n8 - 9 - n10
        n9 - 4 - n10
    }
    println("Матрица пропускной способности потоков:")
    println(graph.trafficVolumeMatrix)
    println("Матрица потока:")
    println(graph.maxFlowMatrix)
    println("Максимальный поток:")
    println(graph.maxFlow)
    println("Насыщенность:")
    println(graph.residualCapacityMatrix)
    println("Минимальный разрез:")
    println("S = ${graph.minimalCutS}, T = ${graph.minimalCutT}")
}