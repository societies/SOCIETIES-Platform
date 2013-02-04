<?php

namespace Societies\TestEngineBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Societies\TestEngineBundle\Entity\PerformanceTest
 *
 * @ORM\Table()
 * @ORM\Entity(repositoryClass="Societies\TestEngineBundle\Entity\PerformanceTestRepository")
 */
class PerformanceTest
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
     * @var string $test_name
     *
     * @ORM\Column(name="test_name", type="string", length=255, unique=true)
     */
    private $test_name;

    /**
     * @var string $test_description
     *
     * @ORM\Column(name="test_description", type="text")
     */
    private $test_description;

    /**
     * @var string $test_developer_name
     *
     * @ORM\Column(name="test_developer_name", type="string", length=255)
     */
    private $test_developer_name;
    
    /**
     * @var string $test_uri
     *
     * @ORM\Column(name="test_uri", type="string", length=255)
     */
    private $test_uri;
    
    /**
     * @ORM\OneToMany(targetEntity="Societies\TestEngineBundle\Entity\PerformanceTestParameters", mappedBy="performanceTest", cascade={"persist"})
     */
    private $performanceTestParameters;
    
    /**
     * @ORM\OneToMany(targetEntity="Societies\TestEngineBundle\Entity\PerformanceTestResult", mappedBy="performanceTest", cascade={"persist"})
     */
    private $performanceTestResults;
    /**
     * Constructor
     */
    public function __construct()
    {
        $this->performanceTestParameters = new \Doctrine\Common\Collections\ArrayCollection();
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
     * Set test_name
     *
     * @param string $testName
     * @return PerformanceTest
     */
    public function setTestName($testName)
    {
        $this->test_name = $testName;
    
        return $this;
    }

    /**
     * Get test_name
     *
     * @return string 
     */
    public function getTestName()
    {
        return $this->test_name;
    }

    /**
     * Set test_description
     *
     * @param string $testDescription
     * @return PerformanceTest
     */
    public function setTestDescription($testDescription)
    {
        $this->test_description = $testDescription;
    
        return $this;
    }

    /**
     * Get test_description
     *
     * @return string 
     */
    public function getTestDescription()
    {
        return $this->test_description;
    }

    /**
     * Set test_developer_name
     *
     * @param string $testDeveloperName
     * @return PerformanceTest
     */
    public function setTestDeveloperName($testDeveloperName)
    {
        $this->test_developer_name = $testDeveloperName;
    
        return $this;
    }

    /**
     * Get test_developer_name
     *
     * @return string 
     */
    public function getTestDeveloperName()
    {
        return $this->test_developer_name;
    }

    /**
     * Set test_uri
     *
     * @param string $testUri
     * @return PerformanceTest
     */
    public function setTestUri($testUri)
    {
        $this->test_uri = $testUri;
    
        return $this;
    }

    /**
     * Get test_uri
     *
     * @return string 
     */
    public function getTestUri()
    {
        return $this->test_uri;
    }

    /**
     * Add performanceTestParameters
     *
     * @param Societies\TestEngineBundle\Entity\PerformanceTestParameters $performanceTestParameters
     * @return PerformanceTest
     */
    public function addPerformanceTestParameter(\Societies\TestEngineBundle\Entity\PerformanceTestParameters $performanceTestParameters)
    {
        $this->performanceTestParameters[] = $performanceTestParameters;
        $performanceTestParameters->setPerformanceTest($this);
        return $this;
    }

    /**
     * Remove performanceTestParameters
     *
     * @param Societies\TestEngineBundle\Entity\PerformanceTestParameters $performanceTestParameters
     */
    public function removePerformanceTestParameter(\Societies\TestEngineBundle\Entity\PerformanceTestParameters $performanceTestParameters)
    {
        $this->performanceTestParameters->removeElement($performanceTestParameters);
    }

    /**
     * Get performanceTestParameters
     *
     * @return Doctrine\Common\Collections\Collection 
     */
    public function getPerformanceTestParameters()
    {
        return $this->performanceTestParameters;
    }

    /**
     * Add performanceTestResults
     *
     * @param Societies\TestEngineBundle\Entity\PerformanceTestResult $performanceTestResults
     * @return PerformanceTest
     */
    public function addPerformanceTestResult(\Societies\TestEngineBundle\Entity\PerformanceTestResult $performanceTestResults)
    {
        $this->performanceTestResults[] = $performanceTestResults;
        $performanceTestResults->setPerformanceTest($this);
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