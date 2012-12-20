<?php

namespace Societies\TestEngineBundle\Controller;

use Societies\TestEngineBundle\Entity\JoinCisTestResult;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;

use Symfony\Component\HttpFoundation\Response;

class DefaultController extends Controller
{
    public function indexAction($name)
    {
        return $this->render('SocietiesTestEngineBundle:Default:index.html.twig', array('name' => $name));
    }
    
    public function joinCisResultAction()
    {
    	//create JoinCisTestResult Entity
    	$joinCisTestResult = new JoinCisTestResult();
    	
    	$joinCisTestResult->setNodeId("emma.societies.local");
    	$joinCisTestResult->setCisId("cis-67c5aaf1-a57a-45f4-9a66-08258745273b.societies.local");
    	$joinCisTestResult->setCssOwnerId("university.societies.local");
    	$joinCisTestResult->setState("success");
    	$joinCisTestResult->setComment("Join CIS has been done successfully");
    	$joinCisTestResult->setTestStartDate("1355926376565");
    	$joinCisTestResult->setTestEndDate("1355926380269");
    	
    	//Get the entity manager to be able to persiste entity into the database
    	$em = $this->getDoctrine()->getEntityManager();
    	
    	//Get the JoinCisTestResultRepository to be able to get JoinCisTestResult entities from the database
    	//$join_cis_test_repository = $em->getRepository('SocietiesTestEngineBundle:JoinCisTestResult');
    	
    	//1st step: we persiste the entity
    	$em->persist($joinCisTestResult);
    	
    	//2nd step: we flush 
    	$em->flush();
    	
    	$response = "id: ".$joinCisTestResult->getId();
    	
    	return new Response("$response", 200);
    }
}
