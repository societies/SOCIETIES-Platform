<?php

/* SocietiesTestEngineBundle::layout.html.twig */
class __TwigTemplate_8fbab831a97c6a288448a9257e9827e0 extends Twig_Template
{
    public function __construct(Twig_Environment $env)
    {
        parent::__construct($env);

        $this->parent = $this->env->loadTemplate("::layout.html.twig");

        $this->blocks = array(
            'title' => array($this, 'block_title'),
            'body' => array($this, 'block_body'),
            'TestEngine_body' => array($this, 'block_TestEngine_body'),
        );
    }

    protected function doGetParent(array $context)
    {
        return "::layout.html.twig";
    }

    protected function doDisplay(array $context, array $blocks = array())
    {
        $this->parent->display($context, array_merge($this->blocks, $blocks));
    }

    // line 3
    public function block_title($context, array $blocks = array())
    {
        $this->displayParentBlock("title", $context, $blocks);
        echo " - Performance Test Engine";
    }

    // line 5
    public function block_body($context, array $blocks = array())
    {
        // line 6
        echo "
\t<h2>Performance Test Engine</h2>

\t";
        // line 9
        $this->displayBlock('TestEngine_body', $context, $blocks);
        // line 12
        echo "
";
    }

    // line 9
    public function block_TestEngine_body($context, array $blocks = array())
    {
        // line 10
        echo "
\t";
    }

    public function getTemplateName()
    {
        return "SocietiesTestEngineBundle::layout.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  53 => 10,  50 => 9,  45 => 12,  43 => 9,  38 => 6,  35 => 5,  28 => 3,  299 => 105,  293 => 103,  289 => 101,  287 => 100,  284 => 99,  270 => 98,  262 => 96,  256 => 94,  253 => 93,  236 => 92,  233 => 91,  227 => 89,  223 => 87,  221 => 86,  202 => 69,  193 => 66,  189 => 65,  185 => 64,  181 => 63,  177 => 62,  173 => 61,  169 => 60,  166 => 59,  162 => 57,  158 => 55,  154 => 53,  152 => 52,  149 => 51,  145 => 50,  124 => 31,  118 => 29,  114 => 27,  112 => 26,  109 => 25,  95 => 24,  87 => 22,  81 => 20,  78 => 19,  61 => 18,  58 => 17,  52 => 15,  48 => 13,  46 => 12,  36 => 6,  33 => 5,  27 => 3,);
    }
}
