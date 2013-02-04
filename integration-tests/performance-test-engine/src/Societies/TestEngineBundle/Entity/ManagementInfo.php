<?php

namespace Societies\TestEngineBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Societies\TestEngineBundle\Entity\ManagementInfo
 *
 * @ORM\Table()
 * @ORM\Entity(repositoryClass="Societies\TestEngineBundle\Entity\ManagementInfoRepository")
 */
class ManagementInfo
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
     * @var string $engine_host
     *
     * @ORM\Column(name="engine_host", type="string", length=255)
     */
    private $engine_host;

    /**
     * @var string $test_mode
     *
     * @ORM\Column(name="test_mode", type="string", length=255)
     */
    private $test_mode;


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
     * Set engine_host
     *
     * @param string $engineHost
     * @return ManagementInfo
     */
    public function setEngineHost($engineHost)
    {
        $this->engine_host = $engineHost;
    
        return $this;
    }

    /**
     * Get engine_host
     *
     * @return string 
     */
    public function getEngineHost()
    {
        return $this->engine_host;
    }

    /**
     * Set test_mode
     *
     * @param string $testMode
     * @return ManagementInfo
     */
    public function setTestMode($testMode)
    {
        $this->test_mode = $testMode;
    
        return $this;
    }

    /**
     * Get test_mode
     *
     * @return string 
     */
    public function getTestMode()
    {
        return $this->test_mode;
    }
}
