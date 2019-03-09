class FlowNetwork(
    private val source: Node,
    private val sink: Node,
    val trafficVolumeMatrix: Matrix<Node, Int>
) {
    val maxFlowMatrix: Matrix<Node, Int>
    val residualCapacityMatrix: Matrix<Node, Int>
    val maxFlow: Int
    val minimalCutS: List<Node>
    val minimalCutT: List<Node>

    init {
        val u = trafficVolumeMatrix.toMutableMatrix()
        val f = SquareMatrix(u.keys) { _, _ -> 0 }
        val ways = mutableListOf<List<Node>>()
        fun findWay(path: List<Node>, curr: Node) {
            if (curr == sink)
                ways += path
            else
                for (i in u.keys)
                    if (i !in path && u[i, curr] != 0)
                        findWay(path + i, i)
        }
        findWay(listOf(source), source)
        var total = 0
        ways.forEach { way ->
            val pairs = way.pairs()
            val minPair = pairs.minBy { u[it.first, it.second] }
            if (minPair != null) {
                val uMin = u[minPair.first, minPair.second]
                pairs.forEach {
                    f[it.first, it.second] += uMin
                    u[it.first, it.second] -= uMin
                    f[it.second, it.first] -= uMin
                }
                total += uMin
            }
        }

        maxFlowMatrix = f.transpose()
        maxFlow = total
        residualCapacityMatrix = SquareMatrix(f.keys) { x, y ->
            trafficVolumeMatrix[x, y] - maxFlowMatrix[x, y]
        }

        val nodes = trafficVolumeMatrix.keys - source - sink
        val combinations = nodes.combinations()
        var minFlow = Int.MAX_VALUE
        var minS = listOf<Node>()
        var minT = listOf<Node>()
        combinations.forEach { it ->
            val curS = listOf(source) + it
            val curT = nodes - it + sink
            var flow = 0
            curS.forEach { s ->
                curT.forEach { t ->
                    if (trafficVolumeMatrix[t, s] > 0)
                        flow += trafficVolumeMatrix[t, s]
                }
            }
            if (flow < minFlow) {
                minFlow = flow
                minS = curS
                minT = curT
            }
        }
        minimalCutS = minS
        minimalCutT = minT
    }

    companion object {
        fun build(func: (FlowNetworkBuilder) -> Unit): FlowNetwork {
            val gb = FlowNetworkBuilder()
            func(gb)
            return gb.build()
        }
    }

    class Node(val name: String) {
        override fun toString() = name
    }
}