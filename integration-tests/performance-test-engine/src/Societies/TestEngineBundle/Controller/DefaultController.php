<?php

namespace Societies\TestEngineBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;

class DefaultController extends Controller
{
    public function indexAction($name)
    {
        return $this->render('SocietiesTestEngineBundle:Default:index.html.twig', array('name' => $name));
    }
}
