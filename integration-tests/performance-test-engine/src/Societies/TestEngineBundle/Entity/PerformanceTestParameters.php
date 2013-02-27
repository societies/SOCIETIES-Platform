<?php

namespace Societies\TestEngineBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Societies\TestEngineBundle\Entity\PerformanceTestParameters
 *
 * @ORM\Table()
 * @ORM\Entity(repositoryClass="Societies\TestEngineBundle\Entity\PerformanceTestParametersRepository")
 */
class PerformanceTestParameters
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
     * @var string $parameter_name
     *
     * @ORM\Column(name="parameter_name", type="string", length=255)
     */
    private $parameter_name;

    /**
     * @var string $parameter_type
     *
     * @ORM\Column(name="parameter_type", type="string", length=255)
     */
    private $parameter_type;

    /**
     * @var string $parameter_description
     *
     * @ORM\Column(name="parameter_description", type="text")
     */
    private $parameter_description;

    /**
     * @var string $parameter_unit
     *
     * @ORM\Column(name="parameter_unit", type="string", length=255)
     */
    private $parameter_unit;
    
    /**
     * @var string $parameter_value
     *
     * @ORM\Column(name="parameter_value", type="text")
     */
    private $parameter_value;

    /**
     * @ORM\ManyToOne(targetEntity="Societies\TestEngineBundle\Entity\PerformanceTest", inversedBy="performanceTestParameters", cascade={"persist"})
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
     * Set parameter_name
     *
     * @param string $parameterName
     * @return PerformanceTestParameters
     */
    public function setParameterName($parameterName)
    {
        $this->parameter_name = $parameterName;
    
        return $this;
    }

    /**
     * Get parameter_name
     *
     * @return string 
     */
    public function getParameterName()
    {
        return $this->parameter_name;
    }

    /**
     * Set parameter_type
     *
     * @param string $parameterType
     * @return PerformanceTestParameters
     */
    public function setParameterType($parameterType)
    {
        $this->parameter_type = $parameterType;
    
        return $this;
    }

    /**
     * Get parameter_type
     *
     * @return string 
     */
    public function getParameterType()
    {
        return $this->parameter_type;
    }

    /**
     * Set parameter_description
     *
     * @param string $parameterDescription
     * @return PerformanceTestParameters
     */
    public function setParameterDescription($parameterDescription)
    {
        $this->parameter_description = $parameterDescription;
    
        return $this;
    }

    /**
     * Get parameter_description
     *
     * @return string 
     */
    public function getParameterDescription()
    {
        return $this->parameter_description;
    }

    /**
     * Set parameter_unit
     *
     * @param string $parameterUnit
     * @return PerformanceTestParameters
     */
    public function setParameterUnit($parameterUnit)
    {
        $this->parameter_unit = $parameterUnit;
    
        return $this;
    }

    /**
     * Get parameter_unit
     *
     * @return string 
     */
    public function getParameterUnit()
    {
        return $this->parameter_unit;
    }

    /**
     * Set parameter_value
     *
     * @param string $parameterValue
     * @return PerformanceTestParameters
     */
    public function setParameterValue($parameterValue)
    {
        $this->parameter_value = $parameterValue;
    
        return $this;
    }

    /**
     * Get parameter_value
     *
     * @return string 
     */
    public function getParameterValue()
    {
        return $this->parameter_value;
    }

    /**
     * Set performanceTest
     *
     * @param Societies\TestEngineBundle\Entity\PerformanceTest $performanceTest
     * @return PerformanceTestParameters
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