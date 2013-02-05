<?php

/* SocietiesTestEngineBundle:TestEngine:test_engine.html.twig */
class __TwigTemplate_e9f6a93cf3426c78816da64f7416a562 extends Twig_Template
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
        echo " - Nodes";
    }

    // line 5
    public function block_body($context, array $blocks = array())
    {
        // line 6
        echo "\t";
        $this->displayParentBlock("body", $context, $blocks);
        echo "
\t
\t<h3>Select a Performance test and Test Nodes</h3>
\t
\t";
        // line 10
        $this->env->loadTemplate("SocietiesTestEngineBundle:TestEngine:test_engine_form.html.twig")->display($context);
        // line 11
        echo "
";
    }

    public function getTemplateName()
    {
        return "SocietiesTestEngineBundle:TestEngine:test_engine.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  47 => 11,  45 => 10,  37 => 6,  34 => 5,  27 => 3,);
    }
}
