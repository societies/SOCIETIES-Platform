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

        // home
        if ($pathinfo === '/index') {
            if (!in_array($this->context->getMethod(), array('GET', 'HEAD'))) {
                $allow = array_merge($allow, array('GET', 'HEAD'));
                goto not_home;
            }

            return array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\DefaultController::indexAction',  '_route' => 'home',);
        }
        not_home:

        // admin
        if ($pathinfo === '/admin') {
            if (!in_array($this->context->getMethod(), array('GET', 'HEAD'))) {
                $allow = array_merge($allow, array('GET', 'HEAD'));
                goto not_admin;
            }

            return array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::adminAction',  '_route' => 'admin',);
        }
        not_admin:

        // admin_mgmt_info
        if ($pathinfo === '/admin/mgmt-info') {
            if (!in_array($this->context->getMethod(), array('GET', 'HEAD'))) {
                $allow = array_merge($allow, array('GET', 'HEAD'));
                goto not_admin_mgmt_info;
            }

            return array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::setMgmtInfoAction',  '_route' => 'admin_mgmt_info',);
        }
        not_admin_mgmt_info:

        // admin_store_mgmt_info
        if ($pathinfo === '/admin/mgmt-info') {
            if ($this->context->getMethod() != 'POST') {
                $allow[] = 'POST';
                goto not_admin_store_mgmt_info;
            }

            return array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::storeMgmtInfoAction',  '_route' => 'admin_store_mgmt_info',);
        }
        not_admin_store_mgmt_info:

        // admin_get_test_nodes
        if ($pathinfo === '/admin/test-nodes') {
            if (!in_array($this->context->getMethod(), array('GET', 'HEAD'))) {
                $allow = array_merge($allow, array('GET', 'HEAD'));
                goto not_admin_get_test_nodes;
            }

            return array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::getAllTestNodesAction',  '_route' => 'admin_get_test_nodes',);
        }
        not_admin_get_test_nodes:

        // admin_add_test_node
        if ($pathinfo === '/admin/test-nodes') {
            if ($this->context->getMethod() != 'POST') {
                $allow[] = 'POST';
                goto not_admin_add_test_node;
            }

            return array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::addNewTestNodeAction',  '_route' => 'admin_add_test_node',);
        }
        not_admin_add_test_node:

        // admin_get_test_node
        if (0 === strpos($pathinfo, '/admin/test-nodes') && preg_match('#^/admin/test\\-nodes/(?<node_id>[^/]+)$#s', $pathinfo, $matches)) {
            if (!in_array($this->context->getMethod(), array('GET', 'HEAD'))) {
                $allow = array_merge($allow, array('GET', 'HEAD'));
                goto not_admin_get_test_node;
            }

            return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::getTestNodeAction',)), array('_route' => 'admin_get_test_node'));
        }
        not_admin_get_test_node:

        // admin_update_test_node
        if (0 === strpos($pathinfo, '/admin/test-nodes') && preg_match('#^/admin/test\\-nodes/(?<node_id>[^/]+)$#s', $pathinfo, $matches)) {
            if ($this->context->getMethod() != 'POST') {
                $allow[] = 'POST';
                goto not_admin_update_test_node;
            }

            return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::updateTestNodeAction',)), array('_route' => 'admin_update_test_node'));
        }
        not_admin_update_test_node:

        // admin_get_performance_tests
        if ($pathinfo === '/admin/performance-tests') {
            if (!in_array($this->context->getMethod(), array('GET', 'HEAD'))) {
                $allow = array_merge($allow, array('GET', 'HEAD'));
                goto not_admin_get_performance_tests;
            }

            return array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::getPerformanceTestsAction',  '_route' => 'admin_get_performance_tests',);
        }
        not_admin_get_performance_tests:

        // admin_add_performance_test
        if ($pathinfo === '/admin/performance-tests') {
            if ($this->context->getMethod() != 'POST') {
                $allow[] = 'POST';
                goto not_admin_add_performance_test;
            }

            return array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::addPerformanceTestAction',  '_route' => 'admin_add_performance_test',);
        }
        not_admin_add_performance_test:

        // admin_get_performance_test_by_id
        if (0 === strpos($pathinfo, '/admin/performance-tests') && preg_match('#^/admin/performance\\-tests/(?<test_id>[^/]+)$#s', $pathinfo, $matches)) {
            if (!in_array($this->context->getMethod(), array('GET', 'HEAD'))) {
                $allow = array_merge($allow, array('GET', 'HEAD'));
                goto not_admin_get_performance_test_by_id;
            }

            return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::getPerformanceTestByIdAction',)), array('_route' => 'admin_get_performance_test_by_id'));
        }
        not_admin_get_performance_test_by_id:

        // admin_update_performance_test_by_id
        if (0 === strpos($pathinfo, '/admin/performance-tests') && preg_match('#^/admin/performance\\-tests/(?<test_id>[^/]+)$#s', $pathinfo, $matches)) {
            if ($this->context->getMethod() != 'POST') {
                $allow[] = 'POST';
                goto not_admin_update_performance_test_by_id;
            }

            return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::updatePerformanceTestAction',)), array('_route' => 'admin_update_performance_test_by_id'));
        }
        not_admin_update_performance_test_by_id:

        // test_engine
        if ($pathinfo === '/test-engine') {
            if (!in_array($this->context->getMethod(), array('GET', 'HEAD'))) {
                $allow = array_merge($allow, array('GET', 'HEAD'));
                goto not_test_engine;
            }

            return array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestEngineController::indexAction',  '_route' => 'test_engine',);
        }
        not_test_engine:

        // test_engine_configure
        if ($pathinfo === '/test-engine') {
            if ($this->context->getMethod() != 'POST') {
                $allow[] = 'POST';
                goto not_test_engine_configure;
            }

            return array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestEngineController::configureTestAction',  '_route' => 'test_engine_configure',);
        }
        not_test_engine_configure:

        // test_engine_send_test
        if ($pathinfo === '/send-test') {
            if ($this->context->getMethod() != 'POST') {
                $allow[] = 'POST';
                goto not_test_engine_send_test;
            }

            return array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestEngineController::sendTestAction',  '_route' => 'test_engine_send_test',);
        }
        not_test_engine_send_test:

        // test_engine_join_cis
        if (0 === strpos($pathinfo, '/test/join-cis') && preg_match('#^/test/join\\-cis/(?<test_id>[^/]+)/(?<css_owner_jid>[^/]+)/(?<cis_jid>[^/]+)$#s', $pathinfo, $matches)) {
            if (!in_array($this->context->getMethod(), array('GET', 'HEAD'))) {
                $allow = array_merge($allow, array('GET', 'HEAD'));
                goto not_test_engine_join_cis;
            }

            return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\DefaultController::joinCisResultAction',)), array('_route' => 'test_engine_join_cis'));
        }
        not_test_engine_join_cis:

        // test_results
        if (0 === strpos($pathinfo, '/test-results') && preg_match('#^/test\\-results/(?<page>[^/]+)$#s', $pathinfo, $matches)) {
            if (!in_array($this->context->getMethod(), array('GET', 'HEAD'))) {
                $allow = array_merge($allow, array('GET', 'HEAD'));
                goto not_test_results;
            }

            return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestResultController::getAllResultsAction',)), array('_route' => 'test_results'));
        }
        not_test_results:

        // test_result_start_test
        if ($pathinfo === '/start-test') {
            if ($this->context->getMethod() != 'POST') {
                $allow[] = 'POST';
                goto not_test_result_start_test;
            }

            return array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestResultController::startTestAction',  '_route' => 'test_result_start_test',);
        }
        not_test_result_start_test:

        // test_result_end_test
        if ($pathinfo === '/end-test') {
            if ($this->context->getMethod() != 'POST') {
                $allow[] = 'POST';
                goto not_test_result_end_test;
            }

            return array (  '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestResultController::endTestAction',  '_route' => 'test_result_end_test',);
        }
        not_test_result_end_test:

        throw 0 < count($allow) ? new MethodNotAllowedException(array_unique($allow)) : new ResourceNotFoundException();
    }
}
