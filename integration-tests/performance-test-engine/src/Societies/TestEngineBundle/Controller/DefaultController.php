<?php

namespace Societies\TestEngineBundle\Controller;

use Symfony\Component\HttpFoundation\Session\Session;

use Societies\TestEngineBundle\Entity\JoinCisTestResult;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;

use Symfony\Component\HttpFoundation\Response;

class DefaultController extends Controller
{
    public function indexAction()
    {
        return $this->render('SocietiesTestEngineBundle:Default:index.html.twig');
    }
}
