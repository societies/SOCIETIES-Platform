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

include_once('src/generator/IGenerator.interface.php');
include_once('Parameter.class.php');
require_once('vendor/twig/twig/lib/Twig/Autoloader.php');

class VirgoGenerator implements IGenerator
{
	private $prefixe;
	private $generatedFolderPath;
	private $templateSubfolderName;
	private $imports;
	private $clientID;
	private $serverID;
	
	private $twig;

	public function __construct($prefixe='', $generatedPath='config', $templateSubfolderName='virgo') {
		// -- Init params
		if (null == $templateSubfolderName || '' == $templateSubfolderName) {
			$templateSubfolderName = 'virgo';
		}
		if (null == $generatedPath || '' == $generatedPath) {
			$generatedPath = 'config';
		}
		$this->templateSubfolderName = ('' == $templateSubfolderName || endsWith($templateSubfolderName, '/') ? $templateSubfolderName : $templateSubfolderName.'/');;
		$this->generatedFolderPath = ('' == $generatedPath || endsWith($generatedPath, '/') ? $generatedPath : $generatedPath.'/');
		$this->prefixe = $prefixe;
		
		// -- Initialisation de moteur de templates
		Twig_Autoloader::register();
		$loader = new Twig_Loader_Filesystem('src/tpl/'.$templateSubfolderName);
		$this->twig = new Twig_Environment($loader);
	}

	public function parse($str) {
		$data = array();
		// -- Parse JSON data
		$parameters = json_decode($str);
		if (json_last_error() !== JSON_ERROR_NONE) {
			echo getJSONError();
		}
		
		// -- Retrieve data
		$params = array();
		if (isset($parameters->host) && '' != $parameters->host) {
			$params['hostName'] = $parameters->host;
		}
		if (isset($parameters->dbPassword) && '' != $parameters->dbPassword) {
			$params['dbPassword'] = $parameters->dbPassword;
		}
		if (isset($parameters->dbHostname) && '' != $parameters->dbHostname) {
			$params['dbHostname'] = $parameters->dbHostname;
		}
		if (isset($parameters->dbUsername) && '' != $parameters->dbUsername) {
			$params['dbUsername'] = $parameters->dbUsername;
		}
		if (isset($parameters->number) && 0 != $parameters->number) {
			$params['number'] = $parameters->number;
			$data = new Parameter($params);
		}
		if (isset($parameters->min) && isset($parameters->max)) {
			for ($i=$parameters->min; $i<=$parameters->max; $i++) {
				$params['number'] = $i;
				$data[] = new Parameter($params);
			}
		}
		// echa($data);
		
		return $data;
	}

	public function generate($data) {
		$content = '';
		$files = array(
				'keystore',
				'org.eclipse.virgo.apps.repository.properties',
				'org.eclipse.virgo.kernel.authentication.config',
				'org.eclipse.virgo.kernel.jmxremote.access.properties',
				'org.eclipse.virgo.kernel.launch.properties',
				'org.eclipse.virgo.kernel.properties',
				'org.eclipse.virgo.kernel.userregion.properties',
				'org.eclipse.virgo.kernel.users.properties',
				'org.eclipse.virgo.medic.properties',
				'org.eclipse.virgo.repository.properties',
				'org.eclipse.virgo.web.properties',
				'org.societies.integration.test.properties',
				'performance.tester.properties',
				'org.societies.platform.properties',
				'osgi.console.ssh.properties',
				'osgi.console.telnet.properties',
				'serviceability.xml',
				'tomcat-server.xml',
				'xc.properties',
				'isstarted.bat',
				'startup.bat',
				'startup.sh',
				'shutdown.bat',
				'shutdown.sh',
				'kill.bat',
				'kill.sh'
		);
		
		if (!is_array($data)) {
			$data = array($data);
		}
		
		$generaciGeneratedFolderPath = $this->generatedFolderPath;
		foreach($data AS $k => $val) {
			// Prepare archive
			$this->generatedFolderPath = substr($generaciGeneratedFolderPath, 0, strlen($generaciGeneratedFolderPath)-1).$val->number.'/';
			$archiveFilename = './gen/'.$this->generatedFolderPath.'../'.$this->prefixe.'config'.$val->number.'.zip';
			$zip = new ZipArchive();
			if($zip->open($archiveFilename, ZipArchive::CREATE) !== true) {
				echo 'Erreur ouverture '.$archiveFilename;
			}
			
			// Prepare recap
			$recapilatif = '<div class="span8 recapitulatif">'."\n\t".'<h2>Messages générés #'.$val->number.'</h2>'."\n\t".'<ul class="nav nav-list">';
			foreach($files AS $file) {
				$recapilatif .=	"\t\t".'<li><a href="#'.$file.$val->number.'">'.$file.'</a>'."\n";
			}
			$recapilatif .= '</ul>'."\n";
			$recapilatif .= '<hr /><a href="'.$archiveFilename.'" class="btn btn-info"><i class="icon-download-alt"></i>
			Télécharger l\'archive</a></div>';
			$content .= $recapilatif;
	
	
			// Generate for each file
			foreach($files AS $filename) {
				// Generate
				$tpl = $this->twig->loadTemplate($filename);
				$generated = ($tpl->render(array('Parameter' => $val)));
				// Save
				if ('startup.bat' == $filename) {
					$filename = 'startup'.$val->number.'.bat';
				}
				if ('startup.sh' == $filename) {
					$filename = 'startup'.$val->number.'.sh';
				}
				if ('isstarted.bat' == $filename) {
					$filename = 'isstarted'.$val->number.'.bat';
				}
				if ('shutdown.bat' == $filename) {
					$filename = 'shutdown'.$val->number.'.bat';
				}
				if ('shutdown.sh' == $filename) {
					$filename = 'shutdown'.$val->number.'.sh';
				}
				if ('kill.bat' == $filename) {
					$filename = 'kill'.$val->number.'.bat';
				}
				if ('kill.sh' == $filename) {
					$filename = 'kill'.$val->number.'.sh';
				}
				$this->saveFiles($filename, $generated);
				$zip->addFile('./gen/'.$this->generatedFolderPath.$filename, $this->generatedFolderPath.$filename);
				// Display the content
				$content .= $this->displayFiles($filename.$val->number, $generated);
			}
	
			$zip->close();
		}
		
		return $content;
	}

	private function saveFiles($filename, $content, $prefixe='') {
		if (!is_dir('./gen/'.$this->generatedFolderPath)) {
			mkdir('./gen/'.$this->generatedFolderPath, 0777, true);
		}
		if (endsWith($prefixe, '/') && !is_dir('./gen/'.$this->generatedFolderPath.$prefixe)) {
			mkdir('./gen/'.$this->generatedFolderPath.$prefixe, 0777, true);
		}
		if (NULL != $content && '' != $content) {
			file_put_contents('./gen/'.$this->generatedFolderPath.$prefixe.$filename, $content);
		}
	}

	private function displayFiles($filename, $content='', $prefixe='') {
		$str = '';
		if (NULL != $content && '' != $content) {
			$str .= '<div class="span4" id="'.$filename.'">'."\n";
			$str .= "\t".'<h2 class="cpp" id="'.$filename.'cpp">'.$prefixe.$filename.'</h2>'."\n".
			"\t\t".'<p><pre>'.htmlspecialchars($content).'</pre></p>'."\n";
			$str .= '</div>'."\n";
		}
		return $str;
	}
}
?>