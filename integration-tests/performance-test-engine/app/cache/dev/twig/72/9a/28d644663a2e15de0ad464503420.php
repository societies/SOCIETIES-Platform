<?php

/* SocietiesTestEngineBundle:Administration:nodes.html.twig */
class __TwigTemplate_729a28d644663a2e15de0ad464503420 extends Twig_Template
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
\t
\t<h3>Test Nodes List</h3>
\t<ul>
  \t\t";
        // line 11
        $context['_parent'] = (array) $context;
        $context['_seq'] = twig_ensure_traversable($this->getContext($context, "nodes_list"));
        foreach ($context['_seq'] as $context["_key"] => $context["node"]) {
            // line 12
            echo "   \t\t\t <li><a href=\"";
            echo twig_escape_filter($this->env, $this->env->getExtension('routing')->getPath("admin_get_test_node", array("node_id" => $this->getAttribute($this->getContext($context, "node"), "nodeId"))), "html", null, true);
            echo "\">";
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "node"), "nodeId"), "html", null, true);
            echo "</a></li>
 \t\t";
        }
        $_parent = $context['_parent'];
        unset($context['_seq'], $context['_iterated'], $context['_key'], $context['node'], $context['_parent'], $context['loop']);
        $context = array_merge($_parent, array_intersect_key($context, $_parent));
        // line 14
        echo "\t</ul>
\t
\t<br/>
\t
\t<h3>Add a New Test Node</h3>
\t
\t\t";
        // line 20
        if (array_key_exists("result", $context)) {
            // line 21
            echo "\t\t\t<p>";
            echo twig_escape_filter($this->env, $this->getContext($context, "result"), "html", null, true);
            echo "</p>
\t\t";
        }
        // line 23
        echo " \t\t";
        $this->env->loadTemplate("SocietiesTestEngineBundle:Administration:test_node_form.html.twig")->display($context);
        // line 24
        echo "\t
";
    }

    public function getTemplateName()
    {
        return "SocietiesTestEngineBundle:Administration:nodes.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  80 => 24,  77 => 23,  71 => 21,  69 => 20,  61 => 14,  50 => 12,  46 => 11,  37 => 6,  34 => 5,  27 => 3,);
    }
}
