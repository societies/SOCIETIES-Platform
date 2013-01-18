<?php

use Symfony\Component\Routing\Exception\MethodNotAllowedException;
use Symfony\Component\Routing\Exception\ResourceNotFoundException;
use Symfony\Component\Routing\RequestContext;

/**
 * appprodUrlMatcher
 *
 * This class has been auto-generated
 * by the Symfony Routing Component.
 */
class appprodUrlMatcher extends Symfony\Bundle\FrameworkBundle\Routing\RedirectableUrlMatcher
{
    /**
     * Constructor.
     */
    public function __construct(RequestContext $context)
    {
        $this->context = $context;
    }

    public function match($pathinfo)
    {
        $allow = array();
        $pathinfo = rawurldecode($pathinfo);

        // societies_test_engine_homepage
        if (0 === strpos($pathinfo, '/hello') && preg_match('#^/hello/(?<name>[^/]+)$#s', $pathinfo, $matches)) {
            return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\DefaultController::indexAction',)), array('_route' => 'societies_test_engine_homepage'));
        }

        // societies_test_engine_join_cis_result
        if (0 === strpos($pathinfo, '/test/join-cis') && preg_match('#^/test/join\\-cis/(?<test_id>[^/]+)/(?<css_owner_jid>[^/]+)/(?<cis_jid>[^/]+)$#s', $pathinfo, $matches)) {
            if (!in_array($this->context->getMethod(), array('GET', 'HEAD'))) {
                $allow = array_merge($allow, array('GET', 'HEAD'));
                goto not_societies_test_engine_join_cis_result;
            }

            return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\DefaultController::joinCisResultAction',)), array('_route' => 'societies_test_engine_join_cis_result'));
        }
        not_societies_test_engine_join_cis_result:

        // societies_test_engine_start_test
        if ($pathinfo === '/start-test') {
            if ($this->context->getMethod() != 'POST') {
                $allow[] = 'POST';
                goto not_societies_test_engine_start_test;
            }

            return array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestResultController::startTestAction',  '_route' => 'societies_test_engine_start_test',);
        }
        not_societies_test_engine_start_test:

        // societies_test_engine_end_test
        if ($pathinfo === '/end-test') {
            if ($this->context->getMethod() != 'POST') {
                $allow[] = 'POST';
                goto not_societies_test_engine_end_test;
            }

            return array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestResultController::endTestAction',  '_route' => 'societies_test_engine_end_test',);
        }
        not_societies_test_engine_end_test:

        throw 0 < count($allow) ? new MethodNotAllowedException(array_unique($allow)) : new ResourceNotFoundException();
    }
}
