using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;

namespace Folkeregister.Web.TypeDeserialization
{
    public class ObjectDescription<TMainType>
    {
        protected IEnumerable<string> _mainTypeNames;
        public ObjectDescription()
        {
            var mainTypes = GetMainTypes().ToList();
            _mainTypeNames = mainTypes.Select(y => y.FullName);
            Types = GetObjectModel(mainTypes);
        }

        private IEnumerable<ObjectModel> GetObjectModel(List<Type> mainTypes)
        {
            var objectModels = new Dictionary<Type, ObjectModel>();
            mainTypes.ForEach(y => GetObjectModel(y, objectModels));
            return objectModels.Values;
        }

        private void GetObjectModel(Type type, Dictionary<Type, ObjectModel> objectModels)
        {
            if (!objectModels.ContainsKey(type) && !IsValueType(type))
            {
                var argumentInfos = GetArgumentInfo(type).ToList();
                objectModels.Add(type, new ObjectModel()
                {
                    Name = type.Name,
                    QualifiedName = type.AssemblyQualifiedName,
                    Type = type.FullName,
                    Arguments = argumentInfos.Select(arg => new Argument()
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

        private IEnumerable<Argument> GetArguments(Type type)
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

        private IEnumerable<Type> GetMainTypes()
        {
            var mainType = typeof (TMainType);
            var mainTypes = AppDomain.CurrentDomain.GetAssemblies()
                .SelectMany(y => y.GetTypes()
                    .Where(t => (mainType.IsInterface && t.GetInterface(mainType.FullName) != null) ||
                                (mainType.IsClass && (t.GetNestedType(mainType.FullName) != null || t == mainType)) && !t.IsAbstract));
            return mainTypes;
        }

        public IEnumerable<ObjectModel> Types { get; set; }
    }
}