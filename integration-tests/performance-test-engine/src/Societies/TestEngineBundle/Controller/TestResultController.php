<?php
namespace Societies\TestEngineBundle\Controller;

use Societies\TestEngineBundle\Entity\PerformanceTestResult;

use Symfony\Component\HttpFoundation\Session\Session;

use Symfony\Component\HttpFoundation\Response;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class TestResultController extends Controller {

	private $_doctrine;
	private $_em;
	
	public function getAllResultsAction($page)
	{
		
		
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
		
		$test_resultsRepo = $this->_em->getRepository('SocietiesTestEngineBundle:PerformanceTestResult');	
		$test_results = $test_resultsRepo->findAll();

		$linePerPage = 10;
		
		$total = count($test_results);
		$nbrPage = ceil($total/$linePerPage);
		
		//echo "nbrPage ".$nbrPage."<br/>";
		
		$currentPage = $page;
		
		if ($currentPage > $nbrPage) 
		{	
			$currentPage = $nbrPage;
		}
		elseif ($currentPage <= 0)
		{
			$currentPage = 1;
		}
		
		//echo "currentPage ".$currentPage."<br/>";
		
		$index = ($currentPage-1)*$linePerPage;
		
		//echo "index ".$index."<br/>";
		
		$test_results = $test_resultsRepo->myFindAll($index, $linePerPage);
		
		
		return $this->render("SocietiesTestEngineBundle:TestResult:test_result.html.twig", array("currentPage"=>$currentPage, "nbrPage"=>$nbrPage, "test_result_list" => $test_results));
	}
	
	public function startTestAction()
	{
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
		
		if (isset($_POST['start_test_response'])) 
		{
			$json_result = $_POST['start_test_response']; 
			
			$result = json_decode($json_result, true);
			
			
			$test_resultsRepo = $this->_em->getRepository('SocietiesTestEngineBundle:PerformanceTestResult');
			
			$test_result = $test_resultsRepo->find($result["test_id"]);
			
			if (null != $test_result) 
			{
				/*date('Y/m/d H:i:s', $result["start_test_date"])*/
				$date = new \DateTime("@".$result["start_test_date"]);
				
				$date->setTimezone(new \DateTimeZone('CET'));
				
				$test_result->setTestStartDate($date->format("Y/m/d H:i:s"));
				$test_result->setStatus($result["status"]);
				$test_result->setStartTestClassName($result["class_name"]);
				$test_result->setNodeJid($result["node_id"]);
				
				$this->_em->flush();
				
				$response = array("status"=>"ok", "response_code"=>"200", "message" =>"Test engine database successfully updated");
					
				$response = json_encode($response);
					
				return new Response("$response", 200);
			}
			else
			{
				$response = array("status"=>"nok", "response_code"=>"200", "message" =>"Test not found in the database!");
					
				$response = json_encode($response);
					
				return new Response("$response", 200);
			}			
		}
		else
		{
			
			$response = array("status"=>"nok", "response_code"=>"200", "message" =>"The request doesn't contain the start_test_response post parameter");
			
			$response = json_encode($response);
			
			return new Response("$response", 200);
		}
		
	}
	
	public function endTestAction()
	{
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
		
		if (isset($_POST['end_test_response'])) 
		{
			$json_result = $_POST['end_test_response'];
			
			$result = json_decode($json_result, true);
			
			$test_resultsRepo = $this->_em->getRepository('SocietiesTestEngineBundle:PerformanceTestResult');
			
			$test_result = $test_resultsRepo->find($result["test_id"]);
			
			if (null != $test_result) 
			{
				
				/*date('Y/m/d H:i:s', $result["end_test_date"])*/
				$date = new \DateTime("@".$result["end_test_date"]);
				
				$date->setTimezone(new \DateTimeZone('CET'));
				
				$test_result->setTestEndDate($date->format("Y/m/d H:i:s"));
				$test_result->setStatus($result["status"]);
				$test_result->setEndTestClassName($result["class_name"]);
				$test_result->setNodeJid($result["node_id"]);
				$test_result->setMessage($result["test_result_message"]);
				
				$this->_em->flush();
				
				$response = array("status"=>"ok", "response_code"=>"200", "message" =>"Test engine database successfully updated");
					
				$response = json_encode($response);
					
				return new Response("$response", 200);
			}
			else
			{
				$response = array("status"=>"nok", "response_code"=>"200", "message" =>"Test not found in the database!");
					
				$response = json_encode($response);
					
				return new Response("$response", 200);
			}			
		}
		else
		{
			$response = array("status"=>"nok", "response_code"=>"200", "message" =>"The request doesn't contain the start_test_response post parameter");
			
			$response = json_encode($response);
			
			return new Response("$response", 200);
		}
	}
}
