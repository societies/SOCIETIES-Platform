<?php

namespace Societies\TestEngineBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Societies\TestEngineBundle\Entity\JoinCisTestResult
 *
 * @ORM\Table(name="join_cis_test_result_table")
 * @ORM\Entity(repositoryClass="Societies\TestEngineBundle\Entity\JoinCisTestResultRepository")
 */
class JoinCisTestResult
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
     * @var string $nodeId
     *
     * @ORM\Column(name="nodeId", type="string", length=255)
     */
    private $nodeId;

    /**
     * @var string $cisId
     *
     * @ORM\Column(name="cisId", type="string", length=255)
     */
    private $cisId;

    /**
     * @var string $cssOwnerId
     *
     * @ORM\Column(name="cssOwnerId", type="string", length=255)
     */
    private $cssOwnerId;

    /**
     * @var string $state
     *
     * @ORM\Column(name="state", type="string", length=255)
     */
    private $state;

    /**
     * @var text $comment
     *
     * @ORM\Column(name="comment", type="text")
     */
    private $comment;

    /**
     * @var string $testStartDate
     *
     * @ORM\Column(name="testStartDate", type="string", length=255)
     */
    private $testStartDate;

    /**
     * @var string $testEndDate
     *
     * @ORM\Column(name="testEndDate", type="string", length=255)
     */
    private $testEndDate;


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
     * Set nodeId
     *
     * @param string $nodeId
     * @return JoinCisTestResult
     */
    public function setNodeId($nodeId)
    {
        $this->nodeId = $nodeId;
    
        return $this;
    }

    /**
     * Get nodeId
     *
     * @return string 
     */
    public function getNodeId()
    {
        return $this->nodeId;
    }

    /**
     * Set cisId
     *
     * @param string $cisId
     * @return JoinCisTestResult
     */
    public function setCisId($cisId)
    {
        $this->cisId = $cisId;
    
        return $this;
    }

    /**
     * Get cisId
     *
     * @return string 
     */
    public function getCisId()
    {
        return $this->cisId;
    }

    /**
     * Set cssOwnerId
     *
     * @param string $cssOwnerId
     * @return JoinCisTestResult
     */
    public function setCssOwnerId($cssOwnerId)
    {
        $this->cssOwnerId = $cssOwnerId;
    
        return $this;
    }

    /**
     * Get cssOwnerId
     *
     * @return string 
     */
    public function getCssOwnerId()
    {
        return $this->cssOwnerId;
    }

    /**
     * Set state
     *
     * @param string $state
     * @return JoinCisTestResult
     */
    public function setState($state)
    {
        $this->state = $state;
    
        return $this;
    }

    /**
     * Get state
     *
     * @return string 
     */
    public function getState()
    {
        return $this->state;
    }

    /**
     * Set comment
     *
     * @param text $comment
     * @return JoinCisTestResult
     */
    public function setComment($comment)
    {
        $this->comment = $comment;
    
        return $this;
    }

    /**
     * Get comment
     *
     * @return text 
     */
    public function getComment()
    {
        return $this->comment;
    }

    /**
     * Set testStartDate
     *
     * @param string $testStartDate
     * @return JoinCisTestResult
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
     * @return JoinCisTestResult
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
    
    
    
    //TODO Here we can add other methods to do some process on the this entity parameteres
}
