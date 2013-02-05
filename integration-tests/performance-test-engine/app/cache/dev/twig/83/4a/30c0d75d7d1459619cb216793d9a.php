<?php

/* SocietiesTestEngineBundle:Administration:mgmt_info.html.twig */
class __TwigTemplate_834a30c0d75d7d1459619cb216793d9a extends Twig_Template
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
    }

    // line 5
    public function block_body($context, array $blocks = array())
    {
        // line 6
        echo "\t";
        $this->displayParentBlock("body", $context, $blocks);
        echo "
\t<h3>Management Information</h3>
\t
\t";
        // line 9
        $this->env->loadTemplate("SocietiesTestEngineBundle:Administration:test_node_form.html.twig")->display($context);
        // line 10
        echo "\t
";
    }

    public function getTemplateName()
    {
        return "SocietiesTestEngineBundle:Administration:mgmt_info.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  45 => 10,  43 => 9,  36 => 6,  33 => 5,  27 => 3,);
    }
}
