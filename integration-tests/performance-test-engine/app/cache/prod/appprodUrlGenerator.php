<?php

use Symfony\Component\Routing\RequestContext;
use Symfony\Component\Routing\Exception\RouteNotFoundException;
use Symfony\Component\HttpKernel\Log\LoggerInterface;

/**
 * appprodUrlGenerator
 *
 * This class has been auto-generated
 * by the Symfony Routing Component.
 */
class appprodUrlGenerator extends Symfony\Component\Routing\Generator\UrlGenerator
{
    static private $declaredRoutes = array(
        'home' => array (  0 =>   array (  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\DefaultController::indexAction',  ),  2 =>   array (    '_method' => 'GET',  ),  3 =>   array (    0 =>     array (      0 => 'text',      1 => '/index',    ),  ),),
        'admin' => array (  0 =>   array (  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::adminAction',  ),  2 =>   array (    '_method' => 'GET',  ),  3 =>   array (    0 =>     array (      0 => 'text',      1 => '/admin',    ),  ),),
        'admin_mgmt_info' => array (  0 =>   array (  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::setMgmtInfoAction',  ),  2 =>   array (    '_method' => 'GET',  ),  3 =>   array (    0 =>     array (      0 => 'text',      1 => '/admin/mgmt-info',    ),  ),),
        'admin_store_mgmt_info' => array (  0 =>   array (  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::storeMgmtInfoAction',  ),  2 =>   array (    '_method' => 'POST',  ),  3 =>   array (    0 =>     array (      0 => 'text',      1 => '/admin/mgmt-info',    ),  ),),
        'admin_get_test_nodes' => array (  0 =>   array (  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::getAllTestNodesAction',  ),  2 =>   array (    '_method' => 'GET',  ),  3 =>   array (    0 =>     array (      0 => 'text',      1 => '/admin/test-nodes',    ),  ),),
        'admin_add_test_node' => array (  0 =>   array (  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::addNewTestNodeAction',  ),  2 =>   array (    '_method' => 'POST',  ),  3 =>   array (    0 =>     array (      0 => 'text',      1 => '/admin/test-nodes',    ),  ),),
        'admin_get_test_node' => array (  0 =>   array (    0 => 'node_id',  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::getTestNodeAction',  ),  2 =>   array (    '_method' => 'GET',  ),  3 =>   array (    0 =>     array (      0 => 'variable',      1 => '/',      2 => '[^/]+',      3 => 'node_id',    ),    1 =>     array (      0 => 'text',      1 => '/admin/test-nodes',    ),  ),),
        'admin_update_test_node' => array (  0 =>   array (    0 => 'node_id',  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::updateTestNodeAction',  ),  2 =>   array (    '_method' => 'POST',  ),  3 =>   array (    0 =>     array (      0 => 'variable',      1 => '/',      2 => '[^/]+',      3 => 'node_id',    ),    1 =>     array (      0 => 'text',      1 => '/admin/test-nodes',    ),  ),),
        'admin_get_performance_tests' => array (  0 =>   array (  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::getPerformanceTestsAction',  ),  2 =>   array (    '_method' => 'GET',  ),  3 =>   array (    0 =>     array (      0 => 'text',      1 => '/admin/performance-tests',    ),  ),),
        'admin_add_performance_test' => array (  0 =>   array (  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::addPerformanceTestAction',  ),  2 =>   array (    '_method' => 'POST',  ),  3 =>   array (    0 =>     array (      0 => 'text',      1 => '/admin/performance-tests',    ),  ),),
        'admin_get_performance_test_by_id' => array (  0 =>   array (    0 => 'test_id',  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::getPerformanceTestByIdAction',  ),  2 =>   array (    '_method' => 'GET',  ),  3 =>   array (    0 =>     array (      0 => 'variable',      1 => '/',      2 => '[^/]+',      3 => 'test_id',    ),    1 =>     array (      0 => 'text',      1 => '/admin/performance-tests',    ),  ),),
        'admin_update_performance_test_by_id' => array (  0 =>   array (    0 => 'test_id',  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\AdministrationController::updatePerformanceTestAction',  ),  2 =>   array (    '_method' => 'POST',  ),  3 =>   array (    0 =>     array (      0 => 'variable',      1 => '/',      2 => '[^/]+',      3 => 'test_id',    ),    1 =>     array (      0 => 'text',      1 => '/admin/performance-tests',    ),  ),),
        'test_engine' => array (  0 =>   array (  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestEngineController::indexAction',  ),  2 =>   array (    '_method' => 'GET',  ),  3 =>   array (    0 =>     array (      0 => 'text',      1 => '/test-engine',    ),  ),),
        'test_engine_configure' => array (  0 =>   array (  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestEngineController::configureTestAction',  ),  2 =>   array (    '_method' => 'POST',  ),  3 =>   array (    0 =>     array (      0 => 'text',      1 => '/test-engine',    ),  ),),
        'test_engine_send_test' => array (  0 =>   array (  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestEngineController::sendTestAction',  ),  2 =>   array (    '_method' => 'POST',  ),  3 =>   array (    0 =>     array (      0 => 'text',      1 => '/send-test',    ),  ),),
        'test_engine_join_cis' => array (  0 =>   array (    0 => 'test_id',    1 => 'css_owner_jid',    2 => 'cis_jid',  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\DefaultController::joinCisResultAction',  ),  2 =>   array (    '_method' => 'GET',  ),  3 =>   array (    0 =>     array (      0 => 'variable',      1 => '/',      2 => '[^/]+',      3 => 'cis_jid',    ),    1 =>     array (      0 => 'variable',      1 => '/',      2 => '[^/]+',      3 => 'css_owner_jid',    ),    2 =>     array (      0 => 'variable',      1 => '/',      2 => '[^/]+',      3 => 'test_id',    ),    3 =>     array (      0 => 'text',      1 => '/test/join-cis',    ),  ),),
        'test_results' => array (  0 =>   array (    0 => 'page',  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestResultController::getAllResultsAction',  ),  2 =>   array (    '_method' => 'GET',  ),  3 =>   array (    0 =>     array (      0 => 'variable',      1 => '/',      2 => '[^/]+',      3 => 'page',    ),    1 =>     array (      0 => 'text',      1 => '/test-results',    ),  ),),
        'test_result_start_test' => array (  0 =>   array (  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestResultController::startTestAction',  ),  2 =>   array (    '_method' => 'POST',  ),  3 =>   array (    0 =>     array (      0 => 'text',      1 => '/start-test',    ),  ),),
        'test_result_end_test' => array (  0 =>   array (  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestResultController::endTestAction',  ),  2 =>   array (    '_method' => 'POST',  ),  3 =>   array (    0 =>     array (      0 => 'text',      1 => '/end-test',    ),  ),),
    );

    /**
     * Constructor.
     */
    public function __construct(RequestContext $context, LoggerInterface $logger = null)
    {
        $this->context = $context;
        $this->logger = $logger;
    }

    public function generate($name, $parameters = array(), $absolute = false)
    {
        if (!isset(self::$declaredRoutes[$name])) {
            throw new RouteNotFoundException(sprintf('Route "%s" does not exist.', $name));
        }

        list($variables, $defaults, $requirements, $tokens) = self::$declaredRoutes[$name];

        return $this->doGenerate($variables, $defaults, $requirements, $tokens, $parameters, $name, $absolute);
    }
}
