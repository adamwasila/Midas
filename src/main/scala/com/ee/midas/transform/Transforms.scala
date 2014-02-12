package com.ee.midas.transform

import org.bson.BSONObject
import TransformType._
import com.ee.midas.hotdeploy.Deployable
import scala.collection.immutable.TreeMap

abstract class Transforms extends Versioner with Deployable[Transforms] {
  type Snippet = BSONObject => BSONObject
  type Snippets = Iterable[Snippet]
  type VersionedSnippets = TreeMap[Double, Snippet]
  var expansions : Map[String, VersionedSnippets]
  var contractions : Map[String, VersionedSnippets]
  
  type ChangeSetCollectionKey = (Long, String)
  var requestExpansions: Map[ChangeSetCollectionKey, Double]
  var requestContractions: Map[ChangeSetCollectionKey, Double]
  
  implicit var transformType: TransformType

  def injectState(fromTransforms: Transforms) = {
    this.expansions = fromTransforms.expansions
    this.contractions = fromTransforms.contractions
    this.transformType = fromTransforms.transformType
  }

  def canBeApplied(fullCollectionName: String): Boolean =
    expansions.keySet.contains(fullCollectionName) || contractions.keySet.contains(fullCollectionName)

  def map(document: BSONObject)(implicit fullCollectionName: String) : BSONObject =  {
    versionedSnippets match {
      case map if map.isEmpty => document
      case vs =>
        val version = getVersion(document) match {
          case Some(version) => version + 1
          case None => 1
        }
        val snippets = snippetsFrom(version, vs)
        applySnippets(snippets, document)
    }
  }

  def versionedSnippets(implicit fullCollectionName: String): VersionedSnippets =
    if(transformType == EXPANSION)
      expansions(fullCollectionName)
    else if(transformType == CONTRACTION)
      contractions(fullCollectionName)
    else TreeMap.empty

  def snippetsFrom(version: Double, versionedSnippets: VersionedSnippets) =
    versionedSnippets.filterKeys(v => v >= version).unzip._2

  def applySnippets(snippets: Snippets, document: BSONObject): BSONObject =
    snippets.foldLeft(document) {
      case (document, snippet) => (snippet andThen version)(document)
    }

  override def toString =
    s"""
      |======================================================================
      |Expansions = ${expansions.size} [${expansions mkString "\n"}]
      |
      |Contractions = ${contractions.size} [${contractions mkString "\n"}]
      |======================================================================
     """.stripMargin
}
