<?php

/* SocietiesTestEngineBundle:Administration:performance_test_form.htm.twig */
class __TwigTemplate_26aba04f083caa87f9f4f130ed871fe4 extends Twig_Template
{
    public function __construct(Twig_Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = array(
        );
    }

    protected function doDisplay(array $context, array $blocks = array())
    {
        // line 1
        echo "<div class=\"well\">
\t<form method=\"post\" ";
        // line 2
        echo $this->env->getExtension('form')->renderer->searchAndRenderBlock($this->getContext($context, "form"), 'enctype');
        echo ">
        ";
        // line 3
        echo $this->env->getExtension('form')->renderer->searchAndRenderBlock($this->getContext($context, "form"), 'widget');
        echo "
        <br /><br />
        <a href=\"#\" id=\"add_parameter\" class=\"btn btn-primary\">Add Parameter</a><br /><br /><br />
        <input value=\"save\" type=\"submit\" class=\"btn btn-primary\" />
    </form>
</div>


<script src=\"http://code.jquery.com/jquery-1.8.2.min.js\"></script>
<script type=\"text/javascript\">
\t\$(document).ready(function() 
\t{
\t\tvar \$container = \$('#societies_testenginebundle_performancetesttype_performanceTestParameters');
\t\tvar \$label = \$container.prev();

\t\t\$container.attr('data-prototype').replace(/__name__label__/, 'Performance test parameter');

\t\tfunction add_parameter() 
\t\t{
\t\t\tindex = \$container.children().length;
\t\t\t\$container.append(\$(\$container.attr('data-prototype').replace(/__name__/g, index)));
\t\t\t\$container.append(\$('<a href=\"#\" id=\"delete_parameter_' + index +'\" class=\"btn btn-danger\">Delete</a>'));

\t\t\t\$('#delete_parameter_' + index).click(function() 
\t\t\t{
\t\t\t\t\$(this).prev().remove();
\t\t\t\t\$(this).remove();
\t\t\t\treturn false;
\t\t\t});
\t\t}
\t\t
\t\tif(\$container.children().length == 0) 
\t\t{
\t\t\t\$label.remove();
\t\t\tadd_parameter();
\t\t}

\t\t\$('#add_parameter').click(function() 
\t\t{
\t\t\tadd_parameter();
\t\t\treturn false;
\t\t\t});
\t});
</script>";
    }

    public function getTemplateName()
    {
        return "SocietiesTestEngineBundle:Administration:performance_test_form.htm.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  21 => 1,  38 => 6,  51 => 22,  47 => 10,  299 => 100,  293 => 96,  290 => 95,  287 => 94,  285 => 93,  280 => 90,  274 => 86,  271 => 85,  268 => 84,  266 => 83,  261 => 80,  247 => 79,  243 => 77,  228 => 75,  220 => 73,  218 => 72,  213 => 70,  209 => 69,  202 => 66,  196 => 63,  183 => 61,  181 => 60,  175 => 58,  158 => 57,  107 => 41,  101 => 34,  80 => 24,  63 => 16,  36 => 6,  156 => 58,  148 => 55,  142 => 50,  140 => 50,  127 => 45,  123 => 44,  115 => 42,  110 => 40,  85 => 28,  65 => 19,  59 => 24,  45 => 10,  103 => 28,  91 => 20,  74 => 22,  70 => 14,  66 => 28,  25 => 4,  89 => 20,  82 => 45,  92 => 39,  86 => 27,  77 => 23,  57 => 22,  19 => 2,  42 => 12,  29 => 5,  26 => 3,  223 => 96,  214 => 90,  210 => 88,  203 => 84,  199 => 83,  194 => 80,  192 => 62,  189 => 78,  187 => 77,  184 => 76,  178 => 72,  170 => 67,  157 => 61,  152 => 59,  145 => 53,  130 => 48,  125 => 49,  119 => 45,  116 => 44,  112 => 43,  102 => 36,  98 => 33,  76 => 24,  73 => 23,  69 => 20,  56 => 12,  32 => 7,  24 => 3,  22 => 3,  23 => 3,  17 => 1,  68 => 30,  61 => 14,  44 => 7,  20 => 2,  161 => 63,  153 => 50,  150 => 49,  147 => 51,  143 => 46,  137 => 45,  129 => 42,  121 => 47,  118 => 43,  113 => 41,  104 => 35,  99 => 33,  94 => 21,  81 => 18,  78 => 24,  72 => 16,  64 => 15,  53 => 10,  50 => 12,  48 => 10,  41 => 7,  39 => 7,  35 => 5,  33 => 5,  30 => 4,  27 => 3,  182 => 70,  176 => 71,  169 => 62,  163 => 58,  160 => 57,  155 => 56,  151 => 54,  149 => 52,  141 => 54,  136 => 47,  134 => 50,  131 => 43,  128 => 47,  120 => 37,  117 => 36,  114 => 35,  109 => 38,  106 => 29,  100 => 30,  96 => 8,  93 => 7,  90 => 28,  87 => 5,  83 => 24,  79 => 25,  71 => 21,  62 => 17,  58 => 23,  55 => 23,  52 => 10,  49 => 14,  46 => 11,  43 => 9,  40 => 7,  37 => 6,  34 => 5,  31 => 4,  28 => 3,);
    }
}
