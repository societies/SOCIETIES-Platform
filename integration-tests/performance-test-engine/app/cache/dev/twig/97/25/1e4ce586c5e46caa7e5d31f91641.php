<?php

/* SocietiesTestEngineBundle:Administration:administration.html.twig */
class __TwigTemplate_97251e4ce586c5e46caa7e5d31f91641 extends Twig_Template
{
    public function __construct(Twig_Environment $env)
    {
        parent::__construct($env);

        $this->parent = $this->env->loadTemplate("SocietiesTestEngineBundle::layout.html.twig");

        $this->blocks = array(
            'title' => array($this, 'block_title'),
            'body' => array($this, 'block_body'),
        );
    }

    protected function doGetParent(array $context)
    {
        return "SocietiesTestEngineBundle::layout.html.twig";
    }

    protected function doDisplay(array $context, array $blocks = array())
    {
        $this->parent->display($context, array_merge($this->blocks, $blocks));
    }

    // line 3
    public function block_title($context, array $blocks = array())
    {
        $this->displayParentBlock("title", $context, $blocks);
        echo " - Test Engine Administration";
    }

    // line 5
    public function block_body($context, array $blocks = array())
    {
        // line 6
        $this->displayParentBlock("body", $context, $blocks);
        echo "

<ul class=\"nav nav-pills nav-stacked\" >
  <li> <a href=\"";
        // line 9
        echo twig_escape_filter($this->env, $this->env->getExtension('routing')->getPath("admin_mgmt_info"), "html", null, true);
        echo "\">Management Info</a> </li>
  <li> <a href=\"";
        // line 10
        echo twig_escape_filter($this->env, $this->env->getExtension('routing')->getPath("admin_get_test_nodes"), "html", null, true);
        echo "\">Test Nodes Management</a> </li>
  <li> <a href=\"";
        // line 11
        echo twig_escape_filter($this->env, $this->env->getExtension('routing')->getPath("admin_get_performance_tests"), "html", null, true);
        echo "\">Performance Test Management</a> </li>
</ul>

";
    }

    public function getTemplateName()
    {
        return "SocietiesTestEngineBundle:Administration:administration.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  51 => 11,  47 => 10,  43 => 9,  37 => 6,  34 => 5,  27 => 3,);
    }
}
