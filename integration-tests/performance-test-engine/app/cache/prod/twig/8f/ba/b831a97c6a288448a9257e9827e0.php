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
        return array (  53 => 10,  51 => 11,  49 => 13,  331 => 100,  325 => 96,  322 => 95,  318 => 94,  315 => 93,  310 => 90,  304 => 86,  301 => 85,  297 => 84,  294 => 83,  289 => 80,  275 => 79,  271 => 77,  256 => 75,  246 => 73,  243 => 72,  237 => 70,  232 => 69,  224 => 66,  214 => 62,  200 => 61,  197 => 60,  190 => 58,  169 => 56,  163 => 52,  154 => 50,  136 => 49,  132 => 47,  129 => 46,  121 => 42,  113 => 35,  80 => 22,  74 => 19,  59 => 12,  52 => 14,  139 => 45,  124 => 42,  118 => 41,  109 => 34,  99 => 33,  84 => 25,  81 => 24,  73 => 20,  69 => 19,  62 => 16,  41 => 7,  123 => 24,  108 => 20,  95 => 18,  90 => 28,  87 => 16,  83 => 23,  26 => 4,  34 => 5,  102 => 34,  78 => 23,  61 => 13,  56 => 12,  38 => 6,  92 => 27,  86 => 24,  46 => 7,  37 => 6,  33 => 5,  29 => 7,  19 => 1,  44 => 12,  27 => 3,  55 => 10,  48 => 7,  45 => 12,  36 => 6,  30 => 4,  25 => 3,  248 => 96,  238 => 90,  234 => 88,  227 => 84,  223 => 83,  218 => 63,  216 => 79,  213 => 78,  210 => 77,  207 => 76,  198 => 71,  192 => 67,  177 => 61,  174 => 60,  171 => 59,  164 => 55,  160 => 51,  155 => 51,  153 => 50,  149 => 47,  146 => 47,  143 => 46,  137 => 45,  126 => 43,  116 => 22,  112 => 37,  107 => 31,  85 => 28,  82 => 27,  77 => 39,  67 => 17,  63 => 14,  32 => 5,  24 => 6,  22 => 4,  23 => 3,  20 => 2,  17 => 1,  201 => 72,  195 => 66,  187 => 62,  181 => 63,  178 => 57,  172 => 57,  168 => 54,  165 => 53,  156 => 51,  151 => 45,  148 => 44,  145 => 43,  142 => 42,  134 => 44,  131 => 44,  128 => 35,  122 => 32,  119 => 31,  111 => 21,  106 => 33,  103 => 19,  100 => 27,  97 => 28,  93 => 24,  89 => 16,  79 => 12,  68 => 15,  64 => 13,  60 => 22,  57 => 15,  54 => 12,  50 => 9,  47 => 10,  43 => 9,  39 => 9,  35 => 5,  31 => 4,  28 => 3,);
    }
}
