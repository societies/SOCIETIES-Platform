<?php

namespace Societies\TestEngineBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Societies\TestEngineBundle\Entity\Nodes
 *
 * @ORM\Table()
 * @ORM\Entity(repositoryClass="Societies\TestEngineBundle\Entity\NodesRepository")
 */
class Nodes
{
    /**
     * @var integer $id
     *
     * @ORM\Column(name="id", type="integer")
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    private $id;

    /**
     * @var string $node_id
     *
     * @ORM\Column(name="node_id", type="string", length=255, unique=true)
     */
    private $node_id;

    /**
     * @var string $node_name
     *
     * @ORM\Column(name="node_name", type="string", length=255)
     */
    private $node_name;
    
    /**
     * @var string $node_host
     *
     * @ORM\Column(name="node_host", type="string", length=255, unique=true)
     */
    private $node_host;

    /**
     * @ORM\OneToMany(targetEntity="Societies\TestEngineBundle\Entity\PerformanceTestResult", mappedBy="node", cascade={"persist"})
     */
    private $performanceTestResults;
    
    /**
     * Constructor
     */
    public function __construct()
    {
        $this->performanceTestResults = new \Doctrine\Common\Collections\ArrayCollection();
    }
    
    /**
     * Get id
     *
     * @return integer 
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * Set node_id
     *
     * @param string $nodeId
     * @return Nodes
     */
    public function setNodeId($nodeId)
    {
        $this->node_id = $nodeId;
    
        return $this;
    }

    /**
     * Get node_id
     *
     * @return string 
     */
    public function getNodeId()
    {
        return $this->node_id;
    }

    /**
     * Set node_name
     *
     * @param string $nodeName
     * @return Nodes
     */
    public function setNodeName($nodeName)
    {
        $this->node_name = $nodeName;
    
        return $this;
    }

    /**
     * Get node_name
     *
     * @return string 
     */
    public function getNodeName()
    {
        return $this->node_name;
    }

    /**
     * Set node_host
     *
     * @param string $nodeHost
     * @return Nodes
     */
    public function setNodeHost($nodeHost)
    {
        $this->node_host = $nodeHost;
    
        return $this;
    }

    /**
     * Get node_host
     *
     * @return string 
     */
    public function getNodeHost()
    {
        return $this->node_host;
    }

    /**
     * Add performanceTestResults
     *
     * @param Societies\TestEngineBundle\Entity\PerformanceTestResult $performanceTestResults
     * @return Nodes
     */
    public function addPerformanceTestResult(\Societies\TestEngineBundle\Entity\PerformanceTestResult $performanceTestResults)
    {
        $this->performanceTestResults[] = $performanceTestResults;
        $performanceTestResults->setNode($this);
        return $this;
    }

    /**
     * Remove performanceTestResults
     *
     * @param Societies\TestEngineBundle\Entity\PerformanceTestResult $performanceTestResults
     */
    public function removePerformanceTestResult(\Societies\TestEngineBundle\Entity\PerformanceTestResult $performanceTestResults)
    {
        $this->performanceTestResults->removeElement($performanceTestResults);
    }

    /**
     * Get performanceTestResults
     *
     * @return Doctrine\Common\Collections\Collection 
     */
    public function getPerformanceTestResults()
    {
        return $this->performanceTestResults;
    }
}