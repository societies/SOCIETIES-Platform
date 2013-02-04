<?php

namespace Societies\TestEngineBundle\Form;

class TestSelection
{
  
    private $test_name;
    private $node_ids_list;


    public function setTestName($testName)
    {
        $this->test_name = $testName;
    
        return $this;
    }

    public function getTestName()
    {
        return $this->test_name;
    }


    public function setNodeIdsList($nodeIdsList)
    {
        $this->node_ids_list = $nodeIdsList;
        return $this;
    }
    
    public function getNodeIdsList()
    {
        return $this->node_ids_list;
    }
}
