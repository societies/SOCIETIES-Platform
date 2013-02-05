<?php

namespace Societies\TestEngineBundle\Form;

class ParamConfig
{

	private $param_values;

	public function __construct()
	{
		//$this->param_values = new \Doctrine\Common\Collections\ArrayCollection();
	}

	public function setParamValues($paramValues)
	{
		$this->param_values[] = $paramValues;

		return $this;
	}

	public function getParamValues()
	{
		return $this->param_values;
	}
}