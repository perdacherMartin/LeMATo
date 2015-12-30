package lemato.clustering

import edu.ucla.sspace.matrix.Matrix
import lemato.nopersistence.SignificanceBucket
import edu.ucla.sspace.clustering.Merge
import edu.ucla.sspace.clustering.HierarchicalAgglomerativeClustering
import static edu.ucla.sspace.clustering.HierarchicalAgglomerativeClustering.ClusterLinkage
import edu.ucla.sspace.common.Similarity.SimType

class VectorSpace{

	Matrix matrix 

	public VectorSpace(Matrix matrix){
		this.matrix = matrix
	}

	DendrogramNode getDendrogramHierachy(List<SignificanceBucket> buckets, ClusterLinkage clusterLinkage, SimType simType){
		Integer remainingClusterId = -1
		Map<Integer, DendrogramNode> remainingNodes = [:]
		buckets.eachWithIndex{ bucket, i ->
			DendrogramNode newNode = new DendrogramNode(bucket)
			remainingNodes.put(i, newNode)
		}

		List<Merge> merges =  new HierarchicalAgglomerativeClustering().buildDendogram(matrix, clusterLinkage, simType )

		merges.each { merge ->

			DendrogramNode mergedNode = 
								new DendrogramNode( (DendrogramNode)remainingNodes.get(merge.remainingCluster()),
									(DendrogramNode)remainingNodes.get(merge.mergedCluster()))

			remainingNodes.remove(merge.remainingCluster())
			remainingNodes.remove(merge.mergedCluster())

			remainingNodes.put(merge.remainingCluster(), mergedNode)
			remainingClusterId = merge.remainingCluster()
		}

		return remainingNodes.get(remainingClusterId)
	}
}