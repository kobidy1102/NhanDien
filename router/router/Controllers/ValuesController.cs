using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web;
using System.Web.Http;
using Microsoft.ProjectOxford.Face;
using Microsoft.ProjectOxford.Face.Contract;
using System.IO;
using System.Net.Http.Headers;
using System.Drawing;

namespace router.Controllers
{
    public class ValuesController : ApiController
    {
        string kq = "Nguyễn Thanh Huy";
        FaceServiceClient faceServiceClient = new FaceServiceClient("5243a5f84e4645579af545b9cba4c496", "https://westcentralus.api.cognitive.microsoft.com/face/v1.0");

        // GET api/values
        //public IEnumerable<string> Get()
        //{
        //    return new string[] { "value1", "value2" };
        //}

        public async Task <string> Get()
        {
            //var result = await RecognitionFaceImgPath("kpop", @"D:\test\gd.jpg");
            // string i = Test();
            return "";
        }


        // GET api/values/5
        public string Get(int id)
        {
            return "value";
        }

        [Route("customers={customerId}")]
        public string Get(string customerId)
        {
            return customerId;
        }
        [HttpPost]
        [Route("data={data}")]
        public string postdata(string data)
        {
            return data;
        }





        [HttpPost, Route("api/recognition/{personGroup}")]
        public async Task<String> IdentityFaceByImage(string personGroup)

        {
            String result = "Co loi trong upload anh!";
            var httpRequest = HttpContext.Current.Request;

            if (httpRequest.Files.Count == 1)
            {
                var postedFile = httpRequest.Files[0];
                Stream file_stream = postedFile.InputStream;
                result = await RecognitionFaceImgPath(personGroup, file_stream);
            }
            
            return result;

        }


        [HttpPost, Route("api/addPersonToGroup/{personGroup}/{name}")]
        public async Task<String> Training(string personGroup, string name)

        {
            String result = "Co loi trong upload anh!";
            var httpRequest = HttpContext.Current.Request;

            if (httpRequest.Files.Count == 1)
            {
                var postedFile = httpRequest.Files[0];
                Stream file_stream = postedFile.InputStream;
                result = await AddPersonToGroup(personGroup, name, file_stream); 
            }

            return result;

        }




        [HttpPost, Route("api/trainingAI/{personGroup}")]
        public async Task<String> TrainAI(string personGroup)

        {
            String result = "Loi training";
            result = await TrainingAI(personGroup);
        
            return result;

        }






        // POST api/values
        public void Post([FromBody]string value)
        {
        }

        // PUT api/values/5
        public void Put(int id, [FromBody]string value)
        {
        }

        // DELETE api/values/5
        public void Delete(int id)
        {
        }







        private async void CreatePersonGroup(String personGroupId, string personGroupName)
        {
            try
            {
                await faceServiceClient.CreatePersonGroupAsync(personGroupId, personGroupName);
                Console.WriteLine("Done " + personGroupName);

            }
            catch (Exception ex)
            {
                Console.WriteLine("Error Create Person Group\n" + ex.Message);
            }
        }



        public async Task<string> AddPersonToGroup(string personGroupId, string Name, Stream s)
        {
            try
            {
                await faceServiceClient.GetPersonGroupAsync(personGroupId);
                CreatePersonResult person = await faceServiceClient.CreatePersonAsync(personGroupId, Name);
              //  DetectFaceAndRegiter(personGroupId, person, pathImage);
                await faceServiceClient.AddPersonFaceAsync(personGroupId, person.PersonId, s);
              //  Console.WriteLine("add " + Name);
                return "Da luu";
            }
            catch (Exception ex)
            {
                //Console.WriteLine("Error Add Person To Group\n" + ex.Message);
                return "co loi xay ra";
            }

            
        }

        private async void DetectFaceAndRegiter(string personGroupId, CreatePersonResult person, string pathImage)
        {
          //  foreach (var imgPath in Directory.GetFiles(pathImage, "*.jpg"))
         //   {
                using (Stream s = File.OpenRead(pathImage))
                {
                    await faceServiceClient.AddPersonFaceAsync(personGroupId, person.PersonId, s);
                }
         //   }
        }

        public async Task<string> TrainingAI(string personGroupId)
        {
            await faceServiceClient.TrainPersonGroupAsync(personGroupId);
            TrainingStatus trainingStatus = null;
            while (true)
            {
                trainingStatus = await faceServiceClient.GetPersonGroupTrainingStatusAsync(personGroupId);
                if (trainingStatus.Status != Status.Running)
                    break;
                await Task.Delay(1000);

            }
            return "Training complete";

        }

        public async Task<string> RecognitionFace(string personGroupId, Stream s)
        {
            var faces = await faceServiceClient.DetectAsync(s);
            var faceIds = faces.Select(face => face.FaceId).ToArray();
            try
            {
                var results = await faceServiceClient.IdentifyAsync(personGroupId, faceIds);
                foreach (var identifyResult in results)
                {
                    // Console.WriteLine(string.Format("Result of face: {0} ", identifyResult.FaceId));
                    if (identifyResult.Candidates.Length == 0)
                    {
                        // Console.WriteLine("No one indentify");
                        kq = "No one indentify";
                    }
                    else
                    {
                        var candidateId = identifyResult.Candidates[0].PersonId;
                        var person = await faceServiceClient.GetPersonAsync(personGroupId, candidateId);
                        // Console.WriteLine(string.Format("Identified as: {0}", person.Name));
                        kq = string.Format("Identified as: {0}", person.Name);
                    }
                }
            }
            catch (Exception ex)
            {
                // Console.WriteLine("Error Recognition Face" + ex.Message);
                kq = "Error Recognition Face" + ex;
            }

            return kq;
        }

        public async Task<string> RecognitionFaceImgPath(string personGroupId, Stream stream)
        {
            var faces = await faceServiceClient.DetectAsync(stream);
            var faceIds = faces.Select(face => face.FaceId).ToArray();
            try
            {
                var results = await faceServiceClient.IdentifyAsync(personGroupId, faceIds);
                foreach (var identifyResult in results)
                {
                    if (identifyResult.Candidates.Length == 0)
                    {
                        kq = "Không nhận diện được";
                    }
                    else
                    {
                        var candidateId = identifyResult.Candidates[0].PersonId;
                        var person = await faceServiceClient.GetPersonAsync(personGroupId, candidateId);
                        kq = string.Format("{0}", person.Name);
                    }
                }
            }
            catch (Exception ex)
            {
                // Console.WriteLine("Error Recognition Face" + ex.Message);
                kq = "Không có khuôn mặt nào được phát hiện";
            }

            return kq;
        }

        public async void deletePersonGroup(string personGroupId)
        {
            try
            {
                await faceServiceClient.DeletePersonGroupAsync(personGroupId);
                Console.WriteLine("Deleted personGroup ");
            }
            catch (Exception ex)
            {
                Console.WriteLine("Delete error" + ex.Message);
            }
        }









    }
}




namespace UploadFile.Custom
{
    public class CustomUploadMultipartFormProvider : MultipartFormDataStreamProvider
    {
        public CustomUploadMultipartFormProvider(string path) : base(path) { }

        public override string GetLocalFileName(HttpContentHeaders headers)
        {
            if (headers != null && headers.ContentDisposition != null)
            {
                return headers
                    .ContentDisposition
                    .FileName.TrimEnd('"').TrimStart('"');
            }

            return base.GetLocalFileName(headers);
        }


    }
}
