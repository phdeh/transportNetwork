import kotlin.reflect.KProperty

class FlowNetworkBuilder {
    private val nodes = mutableListOf<NodeBuilder>()

    private val sourceNode by lazy {NodeBuilder() }
    private val sinkNode by lazy {NodeBuilder() }

    val source by lazy { NodeDelegate(sourceNode) }
    val sink by lazy { NodeDelegate(sinkNode) }
    val node get() = NodeDelegate()

    inner class NodeDelegate(private val nodeBuilder: NodeBuilder = NodeBuilder()) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): NodeBuilder {
            if (nodeBuilder.name == null) {
                if (property.name.matches(Regex("n\\d+")))
                    nodeBuilder.name = property.name.substring(1)
                else
                    nodeBuilder.name = property.name
            }
            return nodeBuilder
        }
    }

    inner class NodeBuilder {
        init {
            nodes += this
        }

        var name: String? = null

        val flows = mutableMapOf<NodeBuilder, Int>()

        operator fun minus(flow: Int): NodeWithFlowBuilder {
            return NodeWithFlowBuilder(this, flow)
        }
    }

    inner class NodeWithFlowBuilder(val node: NodeBuilder, val flow: Int) {
        operator fun minus(node: NodeBuilder) {
            this.node.flows[node] = flow
            node.flows[this.node] = flow
        }

        operator fun plus(flow: Int): NodeWithTwoFlowsBuilder {
            return NodeWithTwoFlowsBuilder(node, this.flow, flow)
        }
    }

    inner class NodeWithTwoFlowsBuilder(val node: NodeBuilder, val flow1: Int, val flow2: Int) {
        operator fun minus(node: NodeBuilder) {
            this.node.flows[node] = flow2
            node.flows[this.node] = flow1
        }
    }

    fun build(): FlowNetwork {
        synchronized(this) {
            val map = mutableMapOf<FlowNetwork.Node, NodeBuilder>()
            val nodes = List(nodes.size) {
                val nodeBuilder = nodes[it]
                val node = FlowNetwork.Node(nodeBuilder.name ?: "UNNAMED_NODE")
                map[node] = nodeBuilder
                node
            }
            val matrix = SquareMatrix<FlowNetwork.Node, Int>(nodes) { x, y ->
                map[x]?.flows?.get(map[y]) ?: 0
            }
            return FlowNetwork(
                nodes.find { map[it] == sourceNode }!!,
                nodes.find { map[it] == sinkNode }!!,
                matrix
                )
        }
    }
}