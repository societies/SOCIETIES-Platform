<?php
namespace Societies\TestEngineBundle\Controller;
use Societies\TestEngineBundle\Entity\PerformanceTestResult;

use Societies\TestEngineBundle\Form\ParamConfigType;

use Societies\TestEngineBundle\Form\ParamConfig;

use Societies\TestEngineBundle\Form\PerformanceTestConfigType;

use Societies\TestEngineBundle\Entity\PerformanceTestParameters;

use Societies\TestEngineBundle\Form\TestSelection;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;

require_once('nusoap.php');

class TestEngineController extends Controller {
	
	
	private $_doctrine;
	private $_em;

	public function indexAction()
	{
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
		
		$performance_testsRepo = $this->_em->getRepository('SocietiesTestEngineBundle:PerformanceTest');
		$performance_tests = $performance_testsRepo->findAll();
		
		$test_nodesRepo = $this->_em->getRepository('SocietiesTestEngineBundle:Nodes');
		$test_nodes = $test_nodesRepo->findAll();
	
		
		
		foreach ($performance_tests as $performance_test)
		{
			$test_list[$performance_test->getId()] = $performance_test->getTestName(); 
		}
		
		foreach ($test_nodes as $test_node)
		{
			$node_list[$test_node->getId()] = $test_node->getNodeId();
		}
		
		
		$testSelection =  new TestSelection();
		$formBuilder = $this->createFormBuilder($testSelection);
		
		$formBuilder
			->add('test_name', 'choice', array('choices'   => $test_list))
			->add('node_ids_list', 'choice', array('choices'   => $node_list, 'multiple'  => true));
		
		$form = $formBuilder->getForm();
		
		return $this->render("SocietiesTestEngineBundle:TestEngine:test_engine.html.twig", array('form' => $form->createView()));
	}
	
	public function configureTestAction()
	{
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
		
		$testSelection = new TestSelection();

		$formBuilder = $this->createFormBuilder($testSelection);
		
		$formBuilder
		->add('test_name', 'text')
		->add('node_ids_list', 'text');
		
		$form = $formBuilder->getForm();
		
		$request = $this->get('request');
		
		$form->bind($request);
		
		$performance_testsRepo = $this->_em->getRepository('SocietiesTestEngineBundle:PerformanceTest');
		$performance_test = $performance_testsRepo->find($testSelection->getTestName());
		
		$test_nodesRepo = $this->_em->getRepository('SocietiesTestEngineBundle:Nodes');
		
		foreach ($testSelection->getNodeIdsList() as $nodeId)
		{
			$nodes_list[] = $test_nodesRepo->find($nodeId); 
		}
		
		$session = $this->get('session');
		
		$session->set("test_nodes", $nodes_list);
		$session->set("performance_test", $performance_test);
		
		$performanceTestParameters = $performance_test->getPerformanceTestParameters();
		
		return $this->render("SocietiesTestEngineBundle:TestEngine:test_config.html.twig", array("performanceTestParameters" => $performanceTestParameters, 'performance_test' => $performance_test, 'node_ids_list'=> $nodes_list));
	}
	
	public function sendTestAction()
	{
		$session = $this->get('session');
		$test_nodes = $session->get("test_nodes");
		$performance_test = $session->get("performance_test");

		
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
		
		$mgmtInfoRepo = $this->_em->getRepository('SocietiesTestEngineBundle:ManagementInfo');
		$performance_testsRepo = $this->_em->getRepository('SocietiesTestEngineBundle:PerformanceTest');
		$test_nodesRepo = $this->_em->getRepository('SocietiesTestEngineBundle:Nodes');
		
		$mgmInfo = $mgmtInfoRepo->find(1);
		
		$performance_test = $performance_testsRepo->find($performance_test->getId());
		
		foreach ($performance_test->getPerformanceTestParameters() as $PerformanceTestParameter)
		{
			$test_params[$PerformanceTestParameter->getParameterName()] = $_POST[$PerformanceTestParameter->getId()];			
			$PerformanceTestParameter->setParameterValue($_POST[$PerformanceTestParameter->getId()]);
		}
		$this->_em->flush();
		
		foreach ($test_nodes as $test_node)
		{
			$test_node = $test_nodesRepo->find($test_node->getId());
			
			$test_result = new PerformanceTestResult();
			
			$test_result->setNode($test_node);
			$test_result->setPerformanceTest($performance_test);
			
			$this->_em->persist($test_result);
			
			$this->_em->flush();
			
			$wsdlPath = $test_node->getNodeHost().$performance_test->getTestUri()."?wsdl"; //Ex: "http://localhost:29092/name-performance-test?wsdl"
			
			$client = new \SoapClient($wsdlPath);
			
			$function_name = $performance_test->getTestName();
				
			$client->$function_name(array("arg0"=>array("testCaseId"=>$test_result->getId(), "performanceTestEngineHost" =>$mgmInfo->getEngineHost(), "testMode"=>$mgmInfo->getTestMode()), "arg1"=>$test_params));
			
			//If using nusoap library
//			$client = new \SoapClient($wsdlPath);
// 			$client->call('joinCisTest', array('arg0'=>array('testCaseId'=>$test_result->getId(), 
// 																		'performanceTestEngineHost' =>'http://localhost/societies_tester/app_dev.php', 
// 																		'testMode'=>'dev_mode'), 
// 													'arg1'=>$test_params));		
// 			echo '<h2>Request</h2>';
// 			echo '<pre>' . htmlspecialchars($client->request, ENT_QUOTES) . '</pre>';
// 			echo '<h2>Response</h2>';
// 			echo '<pre>' . htmlspecialchars($client->response, ENT_QUOTES) . '</pre>';
		}
		
		return $this->redirect( $this->generateUrl('test_results', array("page" => "1" )) );
	}
}
