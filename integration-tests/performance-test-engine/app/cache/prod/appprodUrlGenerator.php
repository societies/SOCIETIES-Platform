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
        'societies_test_engine_homepage' => array (  0 =>   array (    0 => 'name',  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\DefaultController::indexAction',  ),  2 =>   array (  ),  3 =>   array (    0 =>     array (      0 => 'variable',      1 => '/',      2 => '[^/]+',      3 => 'name',    ),    1 =>     array (      0 => 'text',      1 => '/hello',    ),  ),),
        'societies_test_engine_join_cis_result' => array (  0 =>   array (    0 => 'test_id',    1 => 'css_owner_jid',    2 => 'cis_jid',  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\DefaultController::joinCisResultAction',  ),  2 =>   array (    '_method' => 'GET',  ),  3 =>   array (    0 =>     array (      0 => 'variable',      1 => '/',      2 => '[^/]+',      3 => 'cis_jid',    ),    1 =>     array (      0 => 'variable',      1 => '/',      2 => '[^/]+',      3 => 'css_owner_jid',    ),    2 =>     array (      0 => 'variable',      1 => '/',      2 => '[^/]+',      3 => 'test_id',    ),    3 =>     array (      0 => 'text',      1 => '/test/join-cis',    ),  ),),
        'societies_test_engine_start_test' => array (  0 =>   array (  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestResultController::startTestAction',  ),  2 =>   array (    '_method' => 'GET',  ),  3 =>   array (    0 =>     array (      0 => 'text',      1 => '/start-test',    ),  ),),
        'societies_test_engine_end_test' => array (  0 =>   array (  ),  1 =>   array (    '_controller' => 'Societies\\TestEngineBundle\\Controller\\TestResultController::endTestAction',  ),  2 =>   array (    '_method' => 'POST',  ),  3 =>   array (    0 =>     array (      0 => 'text',      1 => '/end-test',    ),  ),),
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
