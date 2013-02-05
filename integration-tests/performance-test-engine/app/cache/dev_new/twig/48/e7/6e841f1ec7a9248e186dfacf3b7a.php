<?php

/* AcmeDemoBundle:Secured:hello.html.twig */
class __TwigTemplate_48e76e841f1ec7a9248e186dfacf3b7a extends Twig_Template
{
    public function __construct(Twig_Environment $env)
    {
        parent::__construct($env);

        $this->parent = $this->env->loadTemplate("AcmeDemoBundle:Secured:layout.html.twig");

        $this->blocks = array(
            'title' => array($this, 'block_title'),
            'content' => array($this, 'block_content'),
        );
    }

    protected function doGetParent(array $context)
    {
        return "AcmeDemoBundle:Secured:layout.html.twig";
    }

    protected function doDisplay(array $context, array $blocks = array())
    {
        // line 11
        $context["code"] = $this->env->getExtension('demo')->getCode($this);
        $this->parent->display($context, array_merge($this->blocks, $blocks));
    }

    // line 3
    public function block_title($context, array $blocks = array())
    {
        echo twig_escape_filter($this->env, ("Hello " . $this->getContext($context, "name")), "html", null, true);
    }

    // line 5
    public function block_content($context, array $blocks = array())
    {
        // line 6
        echo "    <h1>Hello ";
        echo twig_escape_filter($this->env, $this->getContext($context, "name"), "html", null, true);
        echo "!</h1>

    <a href=\"";
        // line 8
        echo twig_escape_filter($this->env, $this->env->getExtension('routing')->getPath("_demo_secured_hello_admin", array("name" => $this->getContext($context, "name"))), "html", null, true);
        echo "\">Hello resource secured for <strong>admin</strong> only.</a>
";
    }

    public function getTemplateName()
    {
        return "AcmeDemoBundle:Secured:hello.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  138 => 39,  84 => 39,  75 => 27,  289 => 101,  284 => 99,  270 => 98,  262 => 96,  256 => 94,  253 => 93,  236 => 92,  233 => 91,  227 => 89,  221 => 86,  193 => 66,  185 => 64,  177 => 62,  173 => 61,  166 => 59,  162 => 57,  154 => 53,  124 => 31,  95 => 45,  105 => 35,  54 => 14,  21 => 1,  38 => 6,  51 => 11,  47 => 10,  299 => 105,  293 => 103,  290 => 95,  287 => 100,  285 => 93,  280 => 90,  274 => 86,  271 => 85,  268 => 84,  266 => 83,  261 => 80,  247 => 79,  243 => 77,  228 => 75,  220 => 73,  218 => 72,  213 => 70,  209 => 69,  202 => 69,  196 => 63,  183 => 61,  181 => 63,  175 => 58,  158 => 55,  107 => 5,  101 => 34,  80 => 37,  63 => 23,  36 => 6,  156 => 58,  148 => 55,  142 => 50,  140 => 50,  127 => 45,  123 => 44,  115 => 42,  110 => 40,  85 => 28,  65 => 19,  59 => 22,  45 => 11,  103 => 28,  91 => 20,  74 => 22,  70 => 14,  66 => 24,  25 => 4,  89 => 20,  82 => 45,  92 => 44,  86 => 41,  77 => 23,  57 => 13,  19 => 2,  42 => 9,  29 => 3,  26 => 3,  223 => 87,  214 => 90,  210 => 88,  203 => 84,  199 => 83,  194 => 80,  192 => 62,  189 => 65,  187 => 77,  184 => 76,  178 => 72,  170 => 67,  157 => 61,  152 => 52,  145 => 50,  130 => 48,  125 => 49,  119 => 30,  116 => 29,  112 => 26,  102 => 36,  98 => 33,  76 => 24,  73 => 23,  69 => 20,  56 => 12,  32 => 7,  24 => 11,  22 => 3,  23 => 3,  17 => 1,  68 => 30,  61 => 18,  44 => 8,  20 => 2,  161 => 63,  153 => 50,  150 => 49,  147 => 51,  143 => 46,  137 => 45,  129 => 42,  121 => 33,  118 => 29,  113 => 28,  104 => 35,  99 => 33,  94 => 21,  81 => 20,  78 => 28,  72 => 16,  64 => 15,  53 => 10,  50 => 10,  48 => 13,  41 => 7,  39 => 7,  35 => 5,  33 => 5,  30 => 4,  27 => 5,  182 => 70,  176 => 71,  169 => 60,  163 => 58,  160 => 57,  155 => 56,  151 => 54,  149 => 51,  141 => 40,  136 => 47,  134 => 50,  131 => 31,  128 => 30,  120 => 37,  117 => 36,  114 => 27,  109 => 25,  106 => 29,  100 => 47,  96 => 8,  93 => 7,  90 => 43,  87 => 22,  83 => 24,  79 => 29,  71 => 21,  62 => 17,  58 => 17,  55 => 12,  52 => 15,  49 => 14,  46 => 9,  43 => 8,  40 => 7,  37 => 5,  34 => 5,  31 => 6,  28 => 3,);
    }
}
