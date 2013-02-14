<?php

/* SocietiesTestEngineBundle:TestEngine:test_config.html.twig */
class __TwigTemplate_f523a9027d9f6d5960dfc761d64e7843 extends Twig_Template
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
        $this->displayParentBlock("body", $context, $blocks);
        echo "

\t<h4>
\t\tYou are going to send a \"";
        // line 9
        echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "performance_test"), "testName"), "html", null, true);
        echo "\" performance test to the following nodes:
\t</h4>
\t
\t<ul>
\t\t";
        // line 13
        $context['_parent'] = (array) $context;
        $context['_seq'] = twig_ensure_traversable($this->getContext($context, "node_ids_list"));
        foreach ($context['_seq'] as $context["_key"] => $context["item"]) {
            // line 14
            echo "\t \t\t
\t \t\t<li> ";
            // line 15
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "item"), "nodeId"), "html", null, true);
            echo " </li>
  \t\t
\t\t";
        }
        $_parent = $context['_parent'];
        unset($context['_seq'], $context['_iterated'], $context['_key'], $context['item'], $context['_parent'], $context['loop']);
        $context = array_merge($_parent, array_intersect_key($context, $_parent));
        // line 18
        echo "\t</ul>

\t
\t<br/>
\t
<div class=\"well\">
<h4>
Test Parameters
</h4>

\t<form method=\"post\" action=\"send-test\">\t
\t\t";
        // line 29
        $context['_parent'] = (array) $context;
        $context['_seq'] = twig_ensure_traversable($this->getContext($context, "performanceTestParameters"));
        foreach ($context['_seq'] as $context["_key"] => $context["item"]) {
            echo "\t \t
\t \t\t
\t \t\t<label class=\"required\" for=\"";
            // line 31
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "item"), "id"), "html", null, true);
            echo "\" >";
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "item"), "parameterName"), "html", null, true);
            echo "</label>
\t \t\t<input type=\"text\" id=\"";
            // line 32
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "item"), "id"), "html", null, true);
            echo "\" name=\"";
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "item"), "id"), "html", null, true);
            echo "\" required=\"required\" value=\"";
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "item"), "parameterValue"), "html", null, true);
            echo "\" />\t
\t \t<br/>
\t\t";
        }
        $_parent = $context['_parent'];
        unset($context['_seq'], $context['_iterated'], $context['_key'], $context['item'], $context['_parent'], $context['loop']);
        $context = array_merge($_parent, array_intersect_key($context, $_parent));
        // line 35
        echo "\t\t
\t\t<input value=\"send\" type=\"submit\" class=\"btn btn-primary\" />
\t</form>
</div>

";
    }

    public function getTemplateName()
    {
        return "SocietiesTestEngineBundle:TestEngine:test_config.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  105 => 35,  92 => 32,  86 => 31,  79 => 29,  66 => 18,  57 => 15,  54 => 14,  50 => 13,  43 => 9,  37 => 6,  34 => 5,  27 => 3,);
    }
}
