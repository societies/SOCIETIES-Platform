<?php

use Symfony\Component\Routing\Exception\MethodNotAllowedException;
use Symfony\Component\Routing\Exception\ResourceNotFoundException;
use Symfony\Component\Routing\RequestContext;

/**
 * appdevUrlMatcher
 *
 * This class has been auto-generated
 * by the Symfony Routing Component.
 */
class appdevUrlMatcher extends Symfony\Bundle\FrameworkBundle\Routing\RedirectableUrlMatcher
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

        // _welcome
        if (rtrim($pathinfo, '/') === '') {
            if (substr($pathinfo, -1) !== '/') {
                return $this->redirect($pathinfo.'/', '_welcome');
            }

            return array (  '_controller' => 'Acme\\DemoBundle\\Controller\\WelcomeController::indexAction',  '_route' => '_welcome',);
        }

        // _demo_login
        if ($pathinfo === '/demo/secured/login') {
            return array (  '_controller' => 'Acme\\DemoBundle\\Controller\\SecuredController::loginAction',  '_route' => '_demo_login',);
        }

        // _security_check
        if ($pathinfo === '/demo/secured/login_check') {
            return array (  '_controller' => 'Acme\\DemoBundle\\Controller\\SecuredController::securityCheckAction',  '_route' => '_security_check',);
        }

        // _demo_logout
        if ($pathinfo === '/demo/secured/logout') {
            return array (  '_controller' => 'Acme\\DemoBundle\\Controller\\SecuredController::logoutAction',  '_route' => '_demo_logout',);
        }

        // acme_demo_secured_hello
        if ($pathinfo === '/demo/secured/hello') {
            return array (  'name' => 'World',  '_controller' => 'Acme\\DemoBundle\\Controller\\SecuredController::helloAction',  '_route' => 'acme_demo_secured_hello',);
        }

        // _demo_secured_hello
        if (0 === strpos($pathinfo, '/demo/secured/hello') && preg_match('#^/demo/secured/hello/(?<name>[^/]+)$#s', $pathinfo, $matches)) {
            return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Acme\\DemoBundle\\Controller\\SecuredController::helloAction',)), array('_route' => '_demo_secured_hello'));
        }

        // _demo_secured_hello_admin
        if (0 === strpos($pathinfo, '/demo/secured/hello/admin') && preg_match('#^/demo/secured/hello/admin/(?<name>[^/]+)$#s', $pathinfo, $matches)) {
            return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Acme\\DemoBundle\\Controller\\SecuredController::helloadminAction',)), array('_route' => '_demo_secured_hello_admin'));
        }

        // _demo
        if (rtrim($pathinfo, '/') === '/demo') {
            if (substr($pathinfo, -1) !== '/') {
                return $this->redirect($pathinfo.'/', '_demo');
            }

            return array (  '_controller' => 'Acme\\DemoBundle\\Controller\\DemoController::indexAction',  '_route' => '_demo',);
        }

        // _demo_hello
        if (0 === strpos($pathinfo, '/demo/hello') && preg_match('#^/demo/hello/(?<name>[^/]+)$#s', $pathinfo, $matches)) {
            return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Acme\\DemoBundle\\Controller\\DemoController::helloAction',)), array('_route' => '_demo_hello'));
        }

        // _demo_contact
        if ($pathinfo === '/demo/contact') {
            return array (  '_controller' => 'Acme\\DemoBundle\\Controller\\DemoController::contactAction',  '_route' => '_demo_contact',);
        }

        // _wdt
        if (0 === strpos($pathinfo, '/_wdt') && preg_match('#^/_wdt/(?<token>[^/]+)$#s', $pathinfo, $matches)) {
            return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Symfony\\Bundle\\WebProfilerBundle\\Controller\\ProfilerController::toolbarAction',)), array('_route' => '_wdt'));
        }

        if (0 === strpos($pathinfo, '/_profiler')) {
            // _profiler_search
            if ($pathinfo === '/_profiler/search') {
                return array (  '_controller' => 'Symfony\\Bundle\\WebProfilerBundle\\Controller\\ProfilerController::searchAction',  '_route' => '_profiler_search',);
            }

            // _profiler_purge
            if ($pathinfo === '/_profiler/purge') {
                return array (  '_controller' => 'Symfony\\Bundle\\WebProfilerBundle\\Controller\\ProfilerController::purgeAction',  '_route' => '_profiler_purge',);
            }

            // _profiler_info
            if (0 === strpos($pathinfo, '/_profiler/info') && preg_match('#^/_profiler/info/(?<about>[^/]+)$#s', $pathinfo, $matches)) {
                return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Symfony\\Bundle\\WebProfilerBundle\\Controller\\ProfilerController::infoAction',)), array('_route' => '_profiler_info'));
            }

            // _profiler_import
            if ($pathinfo === '/_profiler/import') {
                return array (  '_controller' => 'Symfony\\Bundle\\WebProfilerBundle\\Controller\\ProfilerController::importAction',  '_route' => '_profiler_import',);
            }

            // _profiler_export
            if (0 === strpos($pathinfo, '/_profiler/export') && preg_match('#^/_profiler/export/(?<token>[^/\\.]+)\\.txt$#s', $pathinfo, $matches)) {
                return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Symfony\\Bundle\\WebProfilerBundle\\Controller\\ProfilerController::exportAction',)), array('_route' => '_profiler_export'));
            }

            // _profiler_phpinfo
            if ($pathinfo === '/_profiler/phpinfo') {
                return array (  '_controller' => 'Symfony\\Bundle\\WebProfilerBundle\\Controller\\ProfilerController::phpinfoAction',  '_route' => '_profiler_phpinfo',);
            }

            // _profiler_search_results
            if (preg_match('#^/_profiler/(?<token>[^/]+)/search/results$#s', $pathinfo, $matches)) {
                return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Symfony\\Bundle\\WebProfilerBundle\\Controller\\ProfilerController::searchResultsAction',)), array('_route' => '_profiler_search_results'));
            }

            // _profiler
            if (preg_match('#^/_profiler/(?<token>[^/]+)$#s', $pathinfo, $matches)) {
                return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Symfony\\Bundle\\WebProfilerBundle\\Controller\\ProfilerController::panelAction',)), array('_route' => '_profiler'));
            }

            // _profiler_redirect
            if (rtrim($pathinfo, '/') === '/_profiler') {
                if (substr($pathinfo, -1) !== '/') {
                    return $this->redirect($pathinfo.'/', '_profiler_redirect');
                }

                return array (  '_controller' => 'Symfony\\Bundle\\FrameworkBundle\\Controller\\RedirectController::redirectAction',  'route' => '_profiler_search_results',  'token' => 'empty',  'ip' => '',  'url' => '',  'method' => '',  'limit' => '10',  '_route' => '_profiler_redirect',);
            }

        }

        if (0 === strpos($pathinfo, '/_configurator')) {
            // _configurator_home
            if (rtrim($pathinfo, '/') === '/_configurator') {
                if (substr($pathinfo, -1) !== '/') {
                    return $this->redirect($pathinfo.'/', '_configurator_home');
                }

                return array (  '_controller' => 'Sensio\\Bundle\\DistributionBundle\\Controller\\ConfiguratorController::checkAction',  '_route' => '_configurator_home',);
            }

            // _configurator_step
            if (0 === strpos($pathinfo, '/_configurator/step') && preg_match('#^/_configurator/step/(?<index>[^/]+)$#s', $pathinfo, $matches)) {
                return array_merge($this->mergeDefaults($matches, array (  '_controller' => 'Sensio\\Bundle\\DistributionBundle\\Controller\\ConfiguratorController::stepAction',)), array('_route' => '_configurator_step'));
            }

            // _configurator_final
            if ($pathinfo === '/_configurator/final') {
                return array (  '_controller' => 'Sensio\\Bundle\\DistributionBundle\\Controller\\ConfiguratorController::finalAction',  '_route' => '_configurator_final',);
            }

        }

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
