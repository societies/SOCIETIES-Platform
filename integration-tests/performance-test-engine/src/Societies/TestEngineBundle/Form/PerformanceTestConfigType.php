<?php
namespace Societies\TestEngineBundle\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolverInterface;

class PerformanceTestConfigType extends AbstractType {

	public function buildForm(FormBuilderInterface $builder, array $options)
	{
		$builder
		->add('performanceTestParameters', 'collection', array('type' => new TestConfigParamType()));
	}
	
	public function setDefaultOptions(OptionsResolverInterface $resolver)
	{
		$resolver->setDefaults(array(
				'data_class' => 'Societies\TestEngineBundle\Entity\PerformanceTest'
		));
	}
	
	public function getName()
	{
		return 'societies_testenginebundle_performancetestconfigtype';
	}
}
