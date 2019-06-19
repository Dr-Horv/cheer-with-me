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
    "image": "nginxdemos/hello",
    "essential": true,
    "portMappings": [
      {
        "containerPort": 80,
        "hostPort": 80
      }
    ],
    "memory": 500,
    "cpu": 10
  }
]
DEFINITION
}
