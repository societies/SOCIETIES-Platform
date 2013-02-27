<?php

namespace Societies\TestEngineBundle\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolverInterface;

class NodesType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('node_id', 'text')
            ->add('node_name', 'text')
            ->add('node_host', 'text')
        ;
    }

    public function setDefaultOptions(OptionsResolverInterface $resolver)
    {
        $resolver->setDefaults(array(
            'data_class' => 'Societies\TestEngineBundle\Entity\Nodes'
        ));
    }

    public function getName()
    {
        return 'societies_testenginebundle_nodestype';
    }
}
