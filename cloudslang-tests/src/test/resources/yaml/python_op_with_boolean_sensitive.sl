#   (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
#   All rights reserved. This program and the accompanying materials
#   are made available under the terms of the Apache License v2.0 which accompany this distribution.
#
#   The Apache License is available at
#   http://www.apache.org/licenses/LICENSE-2.0

namespace: user.ops

operation:
  name: python_op_with_boolean_sensitive
  python_action:
    script: |
      condition_1 = True
      condition_2 = 1!=1
      condition_3 = 1==1 and False
      condition_4 = 1<>1 or bool(1)
      an_int = 1
  outputs:
    - condition_1
    - condition_2: ${condition_2}
    - condition_3:
        value: ${condition_3}
        sensitive: false
    - condition_4:
        value: ${condition_4}
        sensitive: true
    - an_int
  results:
    - SUCCESS: ${ condition_4 == True }