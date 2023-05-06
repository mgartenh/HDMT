config={
	'Entities' : {
		'unescape_(Ljava/lang/String;)Ljava/lang/String;' : {
			'mutationTypeFailureCount' : {
				'INLINE_CONSTS' : '1',
			},
			'aggregates' : {
				'totalFailures' : '1',
				'totalTime' : '5336',
				'totalMutations' : '1',
			},
			'mutationTypeCount' : {
				'INLINE_CONSTS' : '1',
			},
		},
		'getByName_(Ljava/lang/String;)Ljava/lang/String;' : {
			'mutationTypeFailureCount' : {
				'NEGATE_CONDITIONALS' : '3',
				'INLINE_CONSTS' : '1',
				'REMOVE_CONDITIONALS' : '1',
				'NON_VOID_METHOD_CALLS' : '2',
				'CONSTRUCTOR_CALLS' : '2',
				'NULL_RETURNS' : '5',
				'UOI' : '1',
				'VOID_METHOD_CALLS' : '2',
			},
			'aggregates' : {
				'totalFailures' : '17',
				'totalTime' : '38676',
				'totalMutations' : '17',
			},
			'mutationTypeCount' : {
				'NEGATE_CONDITIONALS' : '3',
				'INLINE_CONSTS' : '1',
				'REMOVE_CONDITIONALS' : '1',
				'NON_VOID_METHOD_CALLS' : '2',
				'CONSTRUCTOR_CALLS' : '2',
				'NULL_RETURNS' : '5',
				'UOI' : '1',
				'VOID_METHOD_CALLS' : '2',
			},
		},
		'isBaseNamedEntity_(Ljava/lang/String;)Z' : {
			'mutationTypeFailureCount' : {
				'INLINE_CONSTS' : '2',
				'REMOVE_CONDITIONALS' : '1',
				'NON_VOID_METHOD_CALLS' : '1',
				'NULL_RETURNS' : '2',
				'PRIMITIVE_RETURNS' : '1',
			},
			'aggregates' : {
				'totalFailures' : '7',
				'totalTime' : '15793',
				'totalMutations' : '7',
			},
			'mutationTypeCount' : {
				'INLINE_CONSTS' : '2',
				'REMOVE_CONDITIONALS' : '1',
				'NON_VOID_METHOD_CALLS' : '1',
				'NULL_RETURNS' : '2',
				'PRIMITIVE_RETURNS' : '1',
			},
		},
		'codepointsForName_(Ljava/lang/String;[I)I' : {
			'mutationTypeFailureCount' : {
				'NEGATE_CONDITIONALS' : '4',
				'REMOVE_CONDITIONALS' : '3',
				'NON_VOID_METHOD_CALLS' : '1',
				'NULL_RETURNS' : '1',
				'PRIMITIVE_RETURNS' : '1',
				'UOI' : '5',
			},
			'aggregates' : {
				'totalFailures' : '15',
				'totalTime' : '49619',
				'totalMutations' : '17',
			},
			'mutationTypeCount' : {
				'NEGATE_CONDITIONALS' : '4',
				'REMOVE_CONDITIONALS' : '3',
				'NON_VOID_METHOD_CALLS' : '1',
				'NULL_RETURNS' : '3',
				'PRIMITIVE_RETURNS' : '1',
				'UOI' : '5',
			},
		},
		'appendEncoded_(Ljava/lang/Appendable;Lorg/jsoup/nodes/Entities$EscapeMode;I)V' : {
			'mutationTypeFailureCount' : {
				'NEGATE_CONDITIONALS' : '4',
				'REMOVE_CONDITIONALS' : '2',
				'NON_VOID_METHOD_CALLS' : '2',
				'NULL_RETURNS' : '1',
				'UOI' : '5',
			},
			'aggregates' : {
				'totalFailures' : '14',
				'totalTime' : '49025',
				'totalMutations' : '14',
			},
			'mutationTypeCount' : {
				'NEGATE_CONDITIONALS' : '4',
				'REMOVE_CONDITIONALS' : '2',
				'NON_VOID_METHOD_CALLS' : '2',
				'NULL_RETURNS' : '1',
				'UOI' : '5',
			},
		},
		'canEncode_(Lorg/jsoup/nodes/Entities$CoreCharset;CLjava/nio/charset/CharsetEncoder;)Z' : {
			'mutationTypeFailureCount' : {
				'NEGATE_CONDITIONALS' : '2',
				'ROR' : '2',
				'INLINE_CONSTS' : '2',
				'REMOVE_CONDITIONALS' : '1',
				'NON_VOID_METHOD_CALLS' : '1',
				'CONDITIONALS_BOUNDARY' : '0',
				'UOI' : '1',
				'FALSE_RETURNS' : '1',
			},
			'aggregates' : {
				'totalFailures' : '10',
				'totalTime' : '84075',
				'totalMutations' : '15',
			},
			'mutationTypeCount' : {
				'NEGATE_CONDITIONALS' : '2',
				'ROR' : '3',
				'INLINE_CONSTS' : '2',
				'REMOVE_CONDITIONALS' : '1',
				'NON_VOID_METHOD_CALLS' : '1',
				'CONDITIONALS_BOUNDARY' : '4',
				'UOI' : '1',
				'FALSE_RETURNS' : '1',
			},
		},
		'isNamedEntity_(Ljava/lang/String;)Z' : {
			'mutationTypeFailureCount' : {
				'REMOVE_CONDITIONALS' : '1',
				'PRIMITIVE_RETURNS' : '1',
				'FALSE_RETURNS' : '3',
				'TRUE_RETURNS' : '2',
			},
			'aggregates' : {
				'totalFailures' : '7',
				'totalTime' : '16552',
				'totalMutations' : '7',
			},
			'mutationTypeCount' : {
				'REMOVE_CONDITIONALS' : '1',
				'PRIMITIVE_RETURNS' : '1',
				'FALSE_RETURNS' : '3',
				'TRUE_RETURNS' : '2',
			},
		},
		'<clinit>_()V' : {
			'mutationTypeFailureCount' : {
				'INLINE_CONSTS' : '1',
				'CONSTRUCTOR_CALLS' : '5',
				'VOID_METHOD_CALLS' : '3',
			},
			'aggregates' : {
				'totalFailures' : '9',
				'totalTime' : '53770',
				'totalMutations' : '11',
			},
			'mutationTypeCount' : {
				'INLINE_CONSTS' : '3',
				'CONSTRUCTOR_CALLS' : '5',
				'VOID_METHOD_CALLS' : '3',
			},
		},
		'escape_(Ljava/lang/String;)Ljava/lang/String;' : {
			'mutationTypeFailureCount' : {
				'NON_VOID_METHOD_CALLS' : '1',
				'NULL_RETURNS' : '0',
			},
			'aggregates' : {
				'totalFailures' : '1',
				'totalTime' : '8502',
				'totalMutations' : '2',
			},
			'mutationTypeCount' : {
				'NON_VOID_METHOD_CALLS' : '1',
				'NULL_RETURNS' : '1',
			},
		},
		'unescape_(Ljava/lang/String;Z)Ljava/lang/String;' : {
			'mutationTypeFailureCount' : {
				'UOI' : '1',
			},
			'aggregates' : {
				'totalFailures' : '1',
				'totalTime' : '11600',
				'totalMutations' : '2',
			},
			'mutationTypeCount' : {
				'UOI' : '2',
			},
		},
		'getCharacterByName_(Ljava/lang/String;)Ljava/lang/Character;' : {
			'mutationTypeFailureCount' : {
				'NON_VOID_METHOD_CALLS' : '2',
				'NULL_RETURNS' : '0',
			},
			'aggregates' : {
				'totalFailures' : '2',
				'totalTime' : '9926',
				'totalMutations' : '3',
			},
			'mutationTypeCount' : {
				'NON_VOID_METHOD_CALLS' : '2',
				'NULL_RETURNS' : '1',
			},
		},
		'escape_(Ljava/lang/Appendable;Ljava/lang/String;Lorg/jsoup/nodes/Document$OutputSettings;ZZZ)V' : {
			'mutationTypeFailureCount' : {
				'ROR' : '5',
				'MATH' : '8',
				'REMOVE_CONDITIONALS' : '9',
				'AOD' : '5',
				'CONDITIONALS_BOUNDARY' : '3',
				'AOR' : '2',
				'VOID_METHOD_CALLS' : '3',
				'NEGATE_CONDITIONALS' : '9',
				'INLINE_CONSTS' : '5',
				'NON_VOID_METHOD_CALLS' : '7',
				'CONSTRUCTOR_CALLS' : '7',
				'NULL_RETURNS' : '4',
				'UOI' : '9',
			},
			'aggregates' : {
				'totalFailures' : '76',
				'totalTime' : '417319',
				'totalMutations' : '93',
			},
			'mutationTypeCount' : {
				'ROR' : '6',
				'MATH' : '8',
				'REMOVE_CONDITIONALS' : '9',
				'AOD' : '9',
				'CONDITIONALS_BOUNDARY' : '6',
				'AOR' : '4',
				'VOID_METHOD_CALLS' : '3',
				'NEGATE_CONDITIONALS' : '10',
				'INLINE_CONSTS' : '11',
				'NON_VOID_METHOD_CALLS' : '7',
				'CONSTRUCTOR_CALLS' : '7',
				'NULL_RETURNS' : '4',
				'UOI' : '9',
			},
		},
		'<init>_()V' : {
			'mutationTypeFailureCount' : {
				'NULL_RETURNS' : '0',
			},
			'aggregates' : {
				'totalFailures' : '0',
				'totalTime' : '6396',
				'totalMutations' : '1',
			},
			'mutationTypeCount' : {
				'NULL_RETURNS' : '1',
			},
		},
		'aggregates' : {
			'totalTime' : '1099921',
		},
		'escape_(Ljava/lang/String;Lorg/jsoup/nodes/Document$OutputSettings;)Ljava/lang/String;' : {
			'mutationTypeFailureCount' : {
				'INLINE_CONSTS' : '1',
				'NON_VOID_METHOD_CALLS' : '3',
				'CONSTRUCTOR_CALLS' : '2',
				'NULL_RETURNS' : '2',
				'VOID_METHOD_CALLS' : '3',
			},
			'aggregates' : {
				'totalFailures' : '11',
				'totalTime' : '56429',
				'totalMutations' : '16',
			},
			'mutationTypeCount' : {
				'INLINE_CONSTS' : '3',
				'NON_VOID_METHOD_CALLS' : '3',
				'CONSTRUCTOR_CALLS' : '2',
				'NULL_RETURNS' : '5',
				'VOID_METHOD_CALLS' : '3',
			},
		},
		'access$000_(Lorg/jsoup/nodes/Entities$EscapeMode;Ljava/lang/String;I)V' : {
			'mutationTypeFailureCount' : {},
			'aggregates' : {
				'totalFailures' : '0',
				'totalTime' : '10',
				'totalMutations' : '0',
			},
			'mutationTypeCount' : {},
		},
		'load_(Lorg/jsoup/nodes/Entities$EscapeMode;Ljava/lang/String;I)V' : {
			'mutationTypeFailureCount' : {
				'NEGATE_CONDITIONALS' : '4',
				'INLINE_CONSTS' : '8',
				'REMOVE_CONDITIONALS' : '6',
				'REMOVE_INCREMENTS' : '5',
				'NON_VOID_METHOD_CALLS' : '5',
				'CONSTRUCTOR_CALLS' : '6',
				'NULL_RETURNS' : '5',
				'UOI' : '10',
				'INCREMENTS' : '6',
				'VOID_METHOD_CALLS' : '9',
			},
			'aggregates' : {
				'totalFailures' : '64',
				'totalTime' : '276854',
				'totalMutations' : '65',
			},
			'mutationTypeCount' : {
				'NEGATE_CONDITIONALS' : '4',
				'INLINE_CONSTS' : '9',
				'REMOVE_CONDITIONALS' : '6',
				'REMOVE_INCREMENTS' : '5',
				'NON_VOID_METHOD_CALLS' : '5',
				'CONSTRUCTOR_CALLS' : '6',
				'NULL_RETURNS' : '5',
				'UOI' : '10',
				'INCREMENTS' : '6',
				'VOID_METHOD_CALLS' : '9',
			},
		},
	},
}