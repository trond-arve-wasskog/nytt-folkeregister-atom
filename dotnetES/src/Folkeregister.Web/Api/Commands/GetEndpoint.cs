using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Reflection;
using Folkeregister.Infrastructure;
using Simple.Web;
using Simple.Web.Behaviors;
using Simple.Web.Links;

namespace Folkeregister.Web.Api.Commands
{
    [UriTemplate("/api/commands")]
    [Root(Rel = "commands", Title = "Command list")]
    public class GetEndpoint : IGet, IOutput<CommandDescription<ICommand>>
    {
        public Status Get()
        {
            Output = new CommandDescription<ICommand>();
            return Status.OK;
        }
        public CommandDescription<ICommand> Output { get; private set; }
    }

    public class ObjectModel2
    {
        public string Type { get; set; }
        public string Name { get; set; }
        public string QualifiedName { get; set; }
        public IEnumerable<Argument2> Arguments { get; set; }
    }

    public class Argument2
    {
        public string Type { get; set; }
        public string Name { get; set; }
        public int Order { get; set; }
        public bool IsEnumerable { get; set; }
    }

    public class ObjectDescription<TMainType>
    {
        protected IEnumerable<string> _mainTypeNames;
        public ObjectDescription()
        {
            var mainTypes = GetMainTypes().ToList();
            _mainTypeNames = mainTypes.Select(y => y.FullName);
            Types = GetObjectModel(mainTypes);
        }

        private IEnumerable<ObjectModel2> GetObjectModel(List<Type> mainTypes)
        {
            var objectModels = new Dictionary<Type, ObjectModel2>();
            mainTypes.ForEach(y => GetObjectModel(y, objectModels));
            return objectModels.Values;
        }

        private void GetObjectModel(Type type, Dictionary<Type, ObjectModel2> objectModels)
        {
            if (!objectModels.ContainsKey(type) && !IsValueType(type))
            {
                var argumentInfos = GetArgumentInfo(type).ToList();
                objectModels.Add(type, new ObjectModel2()
                {
                    Name = type.Name,
                    QualifiedName = type.AssemblyQualifiedName,
                    Type = type.FullName,
                    Arguments = argumentInfos.Select(arg => new Argument2()
                    {
                        Order = arg.Position,
                        IsEnumerable = false,
                        Name = arg.Name,
                        Type = arg.ParameterType.FullName
                    })
                });
                foreach (var parameterInfo in argumentInfos)
                {
                    GetObjectModel(parameterInfo.ParameterType, objectModels);
                }
            }
        }

        private static List<Type> ValueTypes = new List<Type>()
        {
            typeof (int),
            typeof (string),
            typeof (double),
            typeof (Guid)
        };
        private bool IsValueType(Type type)
        {
            return ValueTypes.Contains(type);
        }

        private IEnumerable<ParameterInfo> GetArgumentInfo(Type type)
        {
            var constructor = type.GetConstructors().First();
            var parameters = constructor.GetParameters();
            return parameters;
        }

        private IEnumerable<Argument2> GetArguments(Type type)
        {
            var constructor = type.GetConstructors().First();
            var parameters = constructor.GetParameters();
            var i = 0;
            foreach (var parameter in parameters)
            {
                yield return new Argument2()
                {
                    Name = parameter.Name,
                    Type = parameter.ParameterType.FullName,
                    Order = i
                };
                i++;
            }
        }

        private IEnumerable<Type> GetMainTypes()
        {
            var mainType = typeof (TMainType);
            var mainTypes = AppDomain.CurrentDomain.GetAssemblies()
                .SelectMany(y => y.GetTypes()
                    .Where(t => (mainType.IsInterface && t.GetInterface(mainType.FullName) != null) ||
                            (mainType.IsClass && (t.GetNestedType(mainType.FullName) != null || t == mainType)) && !t.IsAbstract));
            return mainTypes;
        }

        public IEnumerable<ObjectModel2> Types { get; set; }
    }

    public class CommandDescription<TCommand> : ObjectDescription<TCommand>
    {
        public IEnumerable<string> Commands
        {
            get { return _mainTypeNames; }
        }
    }

    public class ObjectModel
    {
        public string Type { get; set; }
        public string Name { get; set; }
        public string QualifiedName { get; set; }
        public IEnumerable<Argument> Arguments { get; set; }

        public object GetObject()
        {
            var type = System.Type.GetType(QualifiedName);
            var result = Activator.CreateInstance(type, GetArguments(Arguments));
            return result;
        }

        private object[] GetArguments(IEnumerable<Argument> arguments)
        {
            var args = arguments.Select(y =>
            {
                var argType = System.Type.GetType(y.Type);
                var typeConverter = TypeDescriptor.GetConverter(argType);
                var value = typeConverter.ConvertFrom(y.Value);
                return value;
            }).ToArray();
            return args;
        }


        public static IEnumerable<ObjectModel> GetObjects<T>()
        {
            var iCommandType = typeof(T);
            var commandTypes = AppDomain.CurrentDomain.GetAssemblies()
                .SelectMany(y => y.GetTypes()
                    .Where(t => t.GetInterface(iCommandType.FullName) != null));
            var commandModels = commandTypes.Select(y => new ObjectModel()
            {
                Type = y.FullName,
                Name = y.Name,
                QualifiedName = y.AssemblyQualifiedName,
                Arguments = CreateArguments(y)
            });
            return commandModels.OrderBy(y => y.Name);
        }

        private static IEnumerable<Argument> CreateArguments(Type type)
        {
            var constructor = type.GetConstructors().First();
            var parameters = constructor.GetParameters();
            var i = 0;
            foreach (var parameter in parameters)
            {
                yield return new Argument()
                {
                    Name = parameter.Name,
                    Type = parameter.ParameterType.FullName,
                    Order = i
                };
                i++;
            }
        }
    }

    public class Argument
    {
        public string Type { get; set; }
        public string Name { get; set; }
        public string Value { get; set; }
        public int Order { get; set; }
    }
}