<?php

namespace Societies\TestEngineBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Societies\TestEngineBundle\Entity\PerformanceTestResult
 *
 * @ORM\Table(name="performance_test_result_table")
 * @ORM\Entity(repositoryClass="Societies\TestEngineBundle\Entity\PerformanceTestResultRepository")
 */
class PerformanceTestResult
{
	
	public function __construct()
	{
		//We can define here default values for this entity parameters
	}
	
    /**
     * @var integer $id
     *
     * @ORM\Column(name="id", type="integer")
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    private $id;

    /**
     * @var string $testStartDate
     *
     * @ORM\Column(name="testStartDate", type="string", length=255, nullable=true)
     */
    private $testStartDate = null;
    
    /**
     * @var string $testEndDate
     *
     * @ORM\Column(name="testEndDate", type="string", length=255, nullable=true)
     */
    private $testEndDate = null;
    
    /**
     * @var string $status
     *
     * @ORM\Column(name="status", type="string", length=255 , nullable=true)
     */
    private $status = null;
    
    /**
     * @var string $message
     *
     * @ORM\Column(name="message", type="text", nullable=true)
     */
    private $message = null;
    
    /**
     * @var string $start_test_class_name
     *
     * @ORM\Column(name="start_test_class_name", type="string", length=255 , nullable=true)
     */
    private $start_test_class_name = null;
    
    /**
     * @var string $end_test_class_name
     *
     * @ORM\Column(name="end_test_class_name", type="string", length=255 , nullable=true)
     */
    private $end_test_class_name = null;
    
    /**
     * @var string $node_jid
     *
     * @ORM\Column(name="node_jid", type="string", length=255 , nullable=true)
     */
    private $node_jid = null;

    /**
     * @ORM\ManyToOne(targetEntity="Societies\TestEngineBundle\Entity\Nodes", inversedBy="performanceTestResults", cascade={"persist"})
     *
     */
    private $node;
    
    /**
     * @ORM\ManyToOne(targetEntity="Societies\TestEngineBundle\Entity\PerformanceTest", inversedBy="performanceTestResults", cascade={"persist"})
     *
     */
    private $performanceTest;


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
     * Set testStartDate
     *
     * @param string $testStartDate
     * @return PerformanceTestResult
     */
    public function setTestStartDate($testStartDate)
    {
        $this->testStartDate = $testStartDate;
    
        return $this;
    }

    /**
     * Get testStartDate
     *
     * @return string 
     */
    public function getTestStartDate()
    {
        return $this->testStartDate;
    }

    /**
     * Set testEndDate
     *
     * @param string $testEndDate
     * @return PerformanceTestResult
     */
    public function setTestEndDate($testEndDate)
    {
        $this->testEndDate = $testEndDate;
    
        return $this;
    }

    /**
     * Get testEndDate
     *
     * @return string 
     */
    public function getTestEndDate()
    {
        return $this->testEndDate;
    }

    /**
     * Set status
     *
     * @param string $status
     * @return PerformanceTestResult
     */
    public function setStatus($status)
    {
        $this->status = $status;
    
        return $this;
    }

    /**
     * Get status
     *
     * @return string 
     */
    public function getStatus()
    {
        return $this->status;
    }

    /**
     * Set message
     *
     * @param string $message
     * @return PerformanceTestResult
     */
    public function setMessage($message)
    {
        $this->message = $message;
    
        return $this;
    }

    /**
     * Get message
     *
     * @return string 
     */
    public function getMessage()
    {
        return $this->message;
    }

    /**
     * Set start_test_class_name
     *
     * @param string $startTestClassName
     * @return PerformanceTestResult
     */
    public function setStartTestClassName($startTestClassName)
    {
        $this->start_test_class_name = $startTestClassName;
    
        return $this;
    }

    /**
     * Get start_test_class_name
     *
     * @return string 
     */
    public function getStartTestClassName()
    {
        return $this->start_test_class_name;
    }

    /**
     * Set end_test_class_name
     *
     * @param string $endTestClassName
     * @return PerformanceTestResult
     */
    public function setEndTestClassName($endTestClassName)
    {
        $this->end_test_class_name = $endTestClassName;
    
        return $this;
    }

    /**
     * Get end_test_class_name
     *
     * @return string 
     */
    public function getEndTestClassName()
    {
        return $this->end_test_class_name;
    }

    /**
     * Set node_jid
     *
     * @param string $nodeJid
     * @return PerformanceTestResult
     */
    public function setNodeJid($nodeJid)
    {
        $this->node_jid = $nodeJid;
    
        return $this;
    }

    /**
     * Get node_jid
     *
     * @return string 
     */
    public function getNodeJid()
    {
        return $this->node_jid;
    }

    /**
     * Set node
     *
     * @param Societies\TestEngineBundle\Entity\Nodes $node
     * @return PerformanceTestResult
     */
    public function setNode(\Societies\TestEngineBundle\Entity\Nodes $node = null)
    {
        $this->node = $node;
    
        return $this;
    }

    /**
     * Get node
     *
     * @return Societies\TestEngineBundle\Entity\Nodes 
     */
    public function getNode()
    {
        return $this->node;
    }

    /**
     * Set performanceTest
     *
     * @param Societies\TestEngineBundle\Entity\PerformanceTest $performanceTest
     * @return PerformanceTestResult
     */
    public function setPerformanceTest(\Societies\TestEngineBundle\Entity\PerformanceTest $performanceTest = null)
    {
        $this->performanceTest = $performanceTest;
    
        return $this;
    }

    /**
     * Get performanceTest
     *
     * @return Societies\TestEngineBundle\Entity\PerformanceTest 
     */
    public function getPerformanceTest()
    {
        return $this->performanceTest;
    }
}