<?php
/**
* Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
* (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
* informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
* COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp.,
* INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM
* ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
* conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
* disclaimer in the documentation and/or other materials provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
* BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
* SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

class Parameter {
	public $number;
	public $configFolderName;
	public $configFolderNameShort;
	public $pickupFolderName;
	public $userName;
	public $hostName;
	public $daNodeName;
	public $daDbName;
	public $secretKey;
	public $dbHostname;
	public $dbPort;
	public $dbName;
	public $dbUsername;
	public $dbPassword;
	public $portNumber;
	public $perfTestportNumber;
	public $openfirePortNumber;
	public $redirectPortNumber;
	
	public function __construct($params=array()) {
		$this->number = intval(@$params['number']);
		$globalUsernameList = !empty($params['globalUsernameList']) ? $params['globalUsernameList'] : array('da', 'university', 'emma', 'arthur', 'mario'); 
		$this->hostName = !empty($params['hostname']) ? $params['hostname'] : 'ict-societies.eu'; 
		$this->secretKey = !empty($params['secretKey']) ? $params['secretKey'] : 'password.societies.local'; 
		$this->daNodeName = !empty($params['daNodeName']) ? $params['daNodeName'] : 'da'; 
		$this->daDbName = !empty($params['daDbName']) ? $params['daDbName'] : 'societiesdb0'; 
		$this->dbHostname = !empty($params['dbHostname']) ? $params['dbHostname'] : '127.0.0.1'; 
		$this->dbPort = !empty($params['dbPort']) ? $params['dbPort'] : '3306'; 
		$this->dbPassword = !empty($params['dbPassword']) ? $params['dbPassword'] : ''; 
		$this->dbUsername = !empty($params['dbUsername']) ? $params['dbUsername'] : 'root'; 
		$this->generateParametersFromNumber($globalUsernameList);
	}
	
	
	public function generateParametersFromNumber($globalUsernameList=array()) {
		$this->configFolderName = 'config'.$this->number.'/';
		$this->configFolderNameShort = 'config'.$this->number;
		$this->pickupFolderName = 'pickup'.$this->number.'/';
		$this->pickupFolderName = 'pickup'.$this->number.'/';
		if (count($globalUsernameList) > $this->number) {
			$this->userName = $globalUsernameList[$this->number];
		}
		else {
			$this->userName = 'user'.$this->number;
		}
		$this->dbName = 'societiesdb'.$this->number;
		if ($this->number < 1000) {
			$numberLength = strlen($this->number.'');
			$this->portNumber = '50'.($numberLength < 3 ? '0' : '').($numberLength < 2 ? '0' : '').$this->number;//'8'.$this->number.'80';
			$this->redirectPortNumber = '55'.($numberLength < 3 ? '0' : '').($numberLength < 2 ? '0' : '').$this->number;//'8'.$this->number.'43';
			$this->openfirePortNumber = '60'.($numberLength < 3 ? '0' : '').($numberLength < 2 ? '0' : '').$this->number;//'9'.$this->number.'75';
			$this->perfTestportNumber = '65'.($numberLength < 3 ? '0' : '').($numberLength < 2 ? '0' : '').$this->number;//'9'.$this->number.'75';
		}
		else {
			die('Hey, I can\'t generate more than 1000 ports! At the moment...');
		}
	}
	
	public function getNumber() { return $this->number; }
	public function setNumber($number) { $this->number = $number; }
	public function getConfigFolderName() { return $this->configFolderName; }
	public function setConfigFolderName($configFolderName) { $this->configFolderName = $configFolderName; }
}