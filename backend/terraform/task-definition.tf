data "aws_ecs_task_definition" "cheerwithme" {
  task_definition = "${aws_ecs_task_definition.cheerwithme.family}"
  depends_on      = ["aws_ecs_task_definition.cheerwithme"]
}

resource "aws_ecs_task_definition" "cheerwithme" {
  family = "cheerwithme"

  container_definitions = <<DEFINITION
[
  {
    "name": "cheerwithme",
    "image": "fredagsdeploy/cheer-with-me:latest",
    "essential": true,
    "portMappings": [
      {
        "containerPort": 8080,
        "hostPort": 0
      }
    ],
    "memory": 400,
    "cpu": 256
  }
]
DEFINITION
}
