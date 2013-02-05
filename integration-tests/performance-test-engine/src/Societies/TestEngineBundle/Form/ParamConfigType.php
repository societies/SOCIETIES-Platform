<?php
namespace Societies\TestEngineBundle\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolverInterface;

class ParamConfigType extends AbstractType 
{
	private $param_name; 
	
	public function __construct($param_name)
	{
		$this->param_name = $param_name;
	}
	
	public function buildForm(FormBuilderInterface $builder, array $options)
	{
		$builder
			->add('param_values', 'collection', array(
														'label' => 'Parameters Configuration',
														'type'=> 'text',
														'options' => array ('label' => $this->param_name)
					
				));
		
		//, array('label' => 'ccsOwnerId')
	}
	
	public function setDefaultOptions(OptionsResolverInterface $resolver)
	{
		$resolver->setDefaults(array(
				'data_class' => 'Societies\TestEngineBundle\Form\ParamConfig'
		));
	}
	
	public function getName()
	{
		return 'societies_testenginebundle_paramconfigtype';
	}
}

