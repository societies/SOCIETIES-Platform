<?php
namespace Societies\TestEngineBundle\Controller;

use Societies\TestEngineBundle\Form\ManagementInfoType;

use Societies\TestEngineBundle\Form\PerformanceTestType;

use Societies\TestEngineBundle\Entity\PerformanceTest;

use Doctrine\DBAL\DBALException;

use Societies\TestEngineBundle\Form\NodesType;

use Societies\TestEngineBundle\Entity\Nodes;

use Symfony\Component\HttpFoundation\Session\Session;

use Symfony\Component\HttpFoundation\Response;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class AdministrationController extends Controller {
	
	private $_doctrine;
	private $_em;
	
	public function adminAction()
	{
		return $this->render("SocietiesTestEngineBundle:Administration:administration.html.twig");
	}
	
	
	//GET /admin/test-nodes
	public function getAllTestNodesAction()
	{
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
		
		$nodesRepo = $this->_em->getRepository('SocietiesTestEngineBundle:Nodes');
		$nodes = $nodesRepo->findAll();
		
		$node = new Nodes();
		
		$node->setNodeId("societies.local");
		$node->setNodeHost("http://localhost:9092");
		
		$form = $this->createForm(new NodesType(), $node);
		
		return $this->render("SocietiesTestEngineBundle:Administration:nodes.html.twig", array("nodes_list"=> $nodes, 'form' => $form->createView()));
	}
	
	//GET /admin/mgmt-info
	public function setMgmtInfoAction()
	{
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
	
		$mgmtInfoRepo = $this->_em->getRepository('SocietiesTestEngineBundle:ManagementInfo');
		$mgmtInfo = $mgmtInfoRepo->find(1);
	
		$form = $this->createForm(new ManagementInfoType(), $mgmtInfo);
	
		return $this->render("SocietiesTestEngineBundle:Administration:mgmt_info.html.twig", array('form'=>$form->createView()));
	}
	
	//POST /admin/mgmt-info
	public function storeMgmtInfoAction()
	{
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
	
		$mgmtInfoRepo = $this->_em->getRepository('SocietiesTestEngineBundle:ManagementInfo');
		$mgmtInfo = $mgmtInfoRepo->find(1);
	
		$form = $this->createForm(new ManagementInfoType(), $mgmtInfo);
		
		$request = $this->get('request');
		if( $request->getMethod() == 'POST' )
		{
			$form->bind($request);
			if( $form->isValid() )
			{
				$em = $this->getDoctrine()->getEntityManager();
				try {
					$em->flush();
				} catch (DBALException $e)
				{
				}
			}
		}
		return $this->render("SocietiesTestEngineBundle:Administration:mgmt_info.html.twig", array('form'=>$form->createView()));
	}

	//POST /admin/test-nodes
	public function addNewTestNodeAction()
	{
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
		
		$nodesRepo = $this->_em->getRepository('SocietiesTestEngineBundle:Nodes');
		$nodes = $nodesRepo->findAll();
		
		$node = new Nodes();
			
		$form = $this->createForm(new NodesType(), $node);
		
		$request = $this->get('request');
		if( $request->getMethod() == 'POST' )
		{
			$form->bind($request);
			if( $form->isValid() )
			{
				$em = $this->getDoctrine()->getEntityManager();
				$em->persist($node);
				try {
					$em->flush();
				} catch (DBALException $e)
				{
					return $this->render("SocietiesTestEngineBundle:Administration:nodes.html.twig", array("nodes_list"=> $nodes, "form" => $form->createView(), "result" =>"Test node already added! Verify the Node id and the Node Host you entered"));
				}
			}
		}
		
		return $this->render("SocietiesTestEngineBundle:Administration:nodes.html.twig", array("nodes_list"=> $nodes, 'form' => $form->createView(), "result" =>"Test Node successfully added!"));
	}
	
	//GET /admin/test-nodes/{node_id}
	public function getTestNodeAction($node_id)
	{
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
		
		$nodesRepo = $this->_em->getRepository('SocietiesTestEngineBundle:Nodes');		
		$nodes = $nodesRepo->findAll();
		
		$node = $nodesRepo->findOneBy(array('node_id' => $node_id));
			
		$form = $this->createForm(new NodesType(), $node);
		
		return $this->render("SocietiesTestEngineBundle:Administration:nodes.html.twig", array("nodes_list"=> $nodes, 'form' => $form->createView()));
	}
	
	//POST /admin/test-nodes/{node_id}
	public function updateTestNodeAction($node_id)
	{
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
		
		$nodesRepo = $this->_em->getRepository('SocietiesTestEngineBundle:Nodes');		
		$nodes = $nodesRepo->findAll();
		
		$node = $nodesRepo->findOneBy(array('node_id' => $node_id));
			
		$form = $this->createForm(new NodesType(), $node);
		
		$request = $this->get('request');
		if( $request->getMethod() == 'POST' )
		{
			$form->bind($request);
			
			if( $form->isValid() )
			{
				$em = $this->getDoctrine()->getEntityManager();
				//$em->persist($node);
				
				try {
					$em->flush();
				} catch (DBALException $e)
				{
					return $this->render("SocietiesTestEngineBundle:Administration:nodes.html.twig", array("nodes_list"=> $nodes, "form" => $form->createView(), "result" =>"Test node already added! Verify the Node id and the Node Host you entered"));
				}
			}
		}
		
		return $this->render("SocietiesTestEngineBundle:Administration:nodes.html.twig", array("nodes_list"=> $nodes, 'form' => $form->createView(), "result" =>"Test Node successfully added!"));
	}
	
// 	public function deleteTestNodeAction()
// 	{
		
// 		return $this->render("SocietiesTestEngineBundle:Administration:nodes.html.twig");
// 	}
	
	//GET admin/performance-tests
	public function getPerformanceTestsAction()
	{
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
		
		$performance_testsRepo = $this->_em->getRepository('SocietiesTestEngineBundle:PerformanceTest');
		$performance_tests = $performance_testsRepo->findAll();
		
		$performance_test = new PerformanceTest();
		
		//Set here default values for the $performance_test object
		
		$form = $this->createForm(new PerformanceTestType(), $performance_test);
		
		return $this->render("SocietiesTestEngineBundle:Administration:performance_test.html.twig", array("performance_tests_list"=> $performance_tests, 'form' => $form->createView()));
	}
	
	//POST admin/performance-tests
	public function addPerformanceTestAction()
	{
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
		
		$performance_testsRepo = $this->_em->getRepository('SocietiesTestEngineBundle:PerformanceTest');
		$performance_tests = $performance_testsRepo->findAll();
		
		$performance_test = new PerformanceTest();
			
		$form = $this->createForm(new PerformanceTestType(), $performance_test);
		
		$request = $this->get('request');
		if( $request->getMethod() == 'POST' )
		{
			$form->bind($request);
			if( $form->isValid() )
			{
				$em = $this->getDoctrine()->getEntityManager();
				$em->persist($performance_test);
				try {
					$em->flush();
				} catch (DBALException $e)
				{
					return $this->render("SocietiesTestEngineBundle:Administration:performance_test.html.twig", array("performance_tests_list"=> $performance_tests, 'form' => $form->createView(), "result" =>"Performance Test already added! Verify parameters you entered"));
				}
			}
		}
		
		return $this->render("SocietiesTestEngineBundle:Administration:performance_test.html.twig", array("performance_tests_list"=> $performance_tests, 'form' => $form->createView(), "result" =>"Performance Test successfully added!"));
	}
	
	//GET admin/performance-tests/{test_id}
	public function getPerformanceTestByIdAction($test_id)
	{
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
		
		$performance_testsRepo = $this->_em->getRepository('SocietiesTestEngineBundle:PerformanceTest');
		$performance_tests = $performance_testsRepo->findAll();
		
		$performance_test = $performance_testsRepo->findOneBy(array("id" => $test_id));
		
		//Set here default values for the $performance_test object
		
		$form = $this->createForm(new PerformanceTestType(), $performance_test);
		
		return $this->render("SocietiesTestEngineBundle:Administration:performance_test.html.twig", array("performance_tests_list"=> $performance_tests, 'form' => $form->createView()));
	}	
	
	//POST admin/performance-tests/{test_id}
	public function updatePerformanceTestAction($test_id)
	{
		$this->_doctrine = $this->getDoctrine();
		$this->_em = $this->_doctrine->getManager();
		
		$performance_testsRepo = $this->_em->getRepository('SocietiesTestEngineBundle:PerformanceTest');
		$performance_tests = $performance_testsRepo->findAll();
		
		$performance_test = $performance_testsRepo->findOneBy(array("id" => $test_id));
			
		$form = $this->createForm(new PerformanceTestType(), $performance_test);
		
		$request = $this->get('request');
		if( $request->getMethod() == 'POST' )
		{
			$form->bind($request);
			if( $form->isValid() )
			{
				$em = $this->getDoctrine()->getEntityManager();
				
				try {
					$em->flush();
				} catch (DBALException $e)
				{
					return $this->render("SocietiesTestEngineBundle:Administration:performance_test.html.twig", array("performance_tests_list"=> $performance_tests, 'form' => $form->createView(), "result" =>"Performance Test already added! Verify parameters you entered"));
				}
			}
		}
		
		return $this->render("SocietiesTestEngineBundle:Administration:performance_test.html.twig", array("performance_tests_list"=> $performance_tests, 'form' => $form->createView(), "result" =>"Performance Test successfully added!"));
	}
	
// 	public function getPerformanceTestsByDeveloperAction()
// 	{
// 		return $this->render("SocietiesTestEngineBundle:Administration:performance_tests.html.twig");
// 	}
	
// 	public function deletePerformanceTestAction()
// 	{
// 		return $this->render("SocietiesTestEngineBundle:Administration:performance_tests.html.twig");
// 	}
	
	
}

	